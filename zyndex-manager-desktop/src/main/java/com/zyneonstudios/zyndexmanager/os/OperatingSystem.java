package com.zyneonstudios.zyndexmanager.os;

public class OperatingSystem {

    private final Type type;
    private final Architecture architecture;

    public OperatingSystem(Type type, Architecture architecture) {
        this.type = type;
        this.architecture = architecture;
    }

    public Architecture getArchitecture() {
        return this.architecture;
    }

    public Type getType() {
        return this.type;
    }

    public enum Type {
        Windows,
        Unix,
        macOS
    }

    public enum Architecture {
        x64,
        aarch64
    }
}