package com.zyneonstudios;

import com.zyneonstudios.zyndexmanager.ZyndexManager;
import com.zyneonstudios.zyndexmanager.os.OperatingSystem;
import live.nerotv.shademebaby.logger.Logger;
import live.nerotv.shademebaby.utils.FileUtil;
import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Main {

    private static String directoryPath;
    private static final OperatingSystem operatingSystem = initialise();
    private static final Logger logger = new Logger("ZXM");
    private static boolean test = false;
    private static boolean online = false;

    private static ZyndexManager zyndexManager;

    public static void main(String[] args) {
        resolveArguments(args);
        extractUI();
        if(online) {
            zyndexManager = new ZyndexManager("https://zyneonstudios.github.io/nexus-zyndex-manager/zyndex-manager-web/");
        } else if(test) {
            zyndexManager = new ZyndexManager("file://B:/Workspaces/IntelliJ/nexus-zyndex-manager/zyndex-manager-web/");
        } else {
            zyndexManager = new ZyndexManager();
        }
        zyndexManager.open();
    }

    private static void extractUI() {
        File folder = new File(directoryPath+"libs/zyneon");
        if(folder.exists()) {
            FileUtil.deleteFolder(folder);
        }
        String path = directoryPath+"libs/zyneon/ui.zip";
        FileUtil.extractResourceFile("ui.zip",path,Main.class);
        FileUtil.unzipFile(path,directoryPath+"libs/zyneon/ui/");
        logger.debug("Deleted UI zip: "+new File(path).delete());
    }

    public static String getDirectoryPath() {
        return URLDecoder.decode(directoryPath, StandardCharsets.UTF_8);
    }

    public static OperatingSystem getOperatingSystem() {
        return operatingSystem;
    }

    public static Logger getLogger() {
        return logger;
    }

    private static void resolveArguments(String[] args) {
        for (String arg : args) {
            if (arg.equalsIgnoreCase("--debug")) {
               logger.setDebugEnabled(true);
            }
            if (arg.equalsIgnoreCase("--test")) {
                test = true;
            }
            if (arg.equalsIgnoreCase("--online")) {
                online = true;
            }
        }
    }

    private static OperatingSystem initialise() {
        OperatingSystem.Type type;
        String folderName = "Zyneon/ZyndexManager";
        String appData;
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            type = OperatingSystem.Type.Windows;
            appData = System.getenv("LOCALAPPDATA");
        } else if (os.contains("mac")) {
            type = OperatingSystem.Type.macOS;
            appData = System.getProperty("user.home") + "/Library/Application Support";
        } else {
            type = OperatingSystem.Type.Unix;
            appData = System.getProperty("user.home") + "/.local/share";
        }
        Path folderPath = Paths.get(appData, folderName);
        try {
            Files.createDirectories(folderPath);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        directoryPath = folderPath.toString().replace("\\","/") + "/";
        OperatingSystem.Architecture architecture;
        String arch = System.getProperty("os.arch");
        ArrayList<String> aarch = new ArrayList<>();
        aarch.add("ARM");
        aarch.add("ARM64");
        aarch.add("aarch64");
        aarch.add("armv6l");
        aarch.add("armv7l");
        architecture = OperatingSystem.Architecture.x64;
        for (String arch_os : aarch) {
            if (arch_os.equalsIgnoreCase(arch)) {
                architecture = OperatingSystem.Architecture.aarch64;
                break;
            }
        }
        return new OperatingSystem(type, architecture);
    }
}