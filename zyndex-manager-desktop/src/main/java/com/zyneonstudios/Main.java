package com.zyneonstudios;

import com.zyneonstudios.zyndexmanager.ZyndexManager;
import com.zyneonstudios.zyndexmanager.os.OperatingSystem;
import live.nerotv.shademebaby.logger.Logger;

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

    private static ZyndexManager zyndexManager;

    public static void main(String[] args) {
        resolveArguments(args);
        zyndexManager = new ZyndexManager();
        zyndexManager.open();
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