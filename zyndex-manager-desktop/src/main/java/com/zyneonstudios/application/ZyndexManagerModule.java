package com.zyneonstudios.application;

import com.zyneonstudios.application.main.ApplicationConfig;
import com.zyneonstudios.application.main.NexusApplication;
import com.zyneonstudios.application.modules.ApplicationModule;
import com.zyneonstudios.zyndexmanager.Main;
import com.zyneonstudios.zyndexmanager.frame.FrameConnector;

import java.util.ArrayList;
import java.util.Arrays;

public class ZyndexManagerModule extends ApplicationModule {

    public ZyndexManagerModule(NexusApplication application) {
        super(application, "zyneon-studios-nexus-team-zyndex-manager", "Zyndex Manager", "2024.5-beta.1", "Zyneon Studios & NEXUS Team: nerotvlive");
        setConnector(new FrameConnector(this));
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {
        Main.main(this);
    }

    @Override
    public void onDisable() {

    }

    public static void main(String[] args) {
        ArrayList<String> arguments = new ArrayList<>(Arrays.stream(args).toList());
        arguments.add("--path:A:/Sync/OneDrive/Projekte/Code/Zyneon-Application/application-main/target/run/");
        arguments.add("--ui:A:/Sync/OneDrive/Projekte/Code/Zyneon-Application/application-ui/content/");
        args = arguments.toArray(new String[0]);
        new ApplicationConfig(args);
        NexusApplication application = new NexusApplication();
        application.getModuleLoader().loadModule(new ZyndexManagerModule(application));
        application.launch();
    }
}