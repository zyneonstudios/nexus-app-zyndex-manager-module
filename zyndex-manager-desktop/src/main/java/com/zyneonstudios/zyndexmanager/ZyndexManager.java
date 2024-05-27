package com.zyneonstudios.zyndexmanager;

import com.formdev.flatlaf.FlatDarkLaf;
import com.zyneonstudios.application.ZyndexManagerModule;
import com.zyneonstudios.application.frame.web.ApplicationFrame;
import com.zyneonstudios.nexus.index.Index;
import com.zyneonstudios.nexus.index.Zyndex;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class ZyndexManager {

    private final ZyndexManagerModule module;
    private final ApplicationFrame frame;
    private static ArrayList<Index> indexes;
    private static String basePath = "file://"+Main.getDirectoryPath()+"libs/zyneon/ui/";

    public ZyndexManager(ZyndexManagerModule module) {
        try {
            FlatDarkLaf.setup();
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception ignore) {}
        this.module = module;
        frame = (ApplicationFrame) module.getApplication().getFrame();
    }

    public ZyndexManager(ZyndexManagerModule module, String ui) {
        basePath = ui;
        try {
            FlatDarkLaf.setup();
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception ignore) {}
        this.module = module;
        frame = (ApplicationFrame) module.getApplication().getFrame();
    }

    public void open() {
        frame.getBrowser().loadURL(basePath+"index.html");
    }

    public static ArrayList<Index> getIndexes() {
        return indexes;
    }

    public static void loadIndexes() {
        indexes = new ArrayList<>();
        File indexFolder = new File(Main.getDirectoryPath()+"indexes/");
        if(!indexFolder.exists()) {
            Main.getLogger().log("Created indexes folder: " + indexFolder.mkdirs());
        }
        for(File folder : Objects.requireNonNull(indexFolder.listFiles())) {
            if(folder.isDirectory()) {
                File index = new File(folder.getAbsolutePath()+"/index.json");
                if(index.exists()) {
                    indexes.add(new Zyndex(index));
                }
            }
        }
    }

    public static String getBasePath() {
        return basePath;
    }
}