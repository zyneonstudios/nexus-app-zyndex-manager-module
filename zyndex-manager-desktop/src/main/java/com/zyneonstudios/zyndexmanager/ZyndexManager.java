package com.zyneonstudios.zyndexmanager;

import com.formdev.flatlaf.FlatDarkLaf;
import com.zyneonstudios.Main;
import com.zyneonstudios.nexus.index.Index;
import com.zyneonstudios.nexus.index.Zyndex;
import com.zyneonstudios.zyndexmanager.frame.ZyndexWebFrame;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class ZyndexManager {

    private final ZyndexWebFrame frame;
    private static ArrayList<Index> indexes;
    public static final String basePath = "B:/Workspaces/IntelliJ/nexus-zyndex-manager/zyndex-manager-web/";

    public ZyndexManager() {
        try {
            FlatDarkLaf.setup();
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception ignore) {}
        frame = new ZyndexWebFrame(basePath+"index.html", Main.getDirectoryPath()+"libs/jcef/");
    }

    public ZyndexWebFrame getFrame() {
        return frame;
    }

    public void open() {
        frame.setSize(1152,648);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
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
}