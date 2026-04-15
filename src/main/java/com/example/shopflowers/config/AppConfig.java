package com.example.shopflowers.config;

public final class AppConfig {

    private static AppMode mode = AppMode.FULL;

    private AppConfig() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static AppMode getMode() {
        return mode;
    }

    public static void setMode(AppMode newMode) {
        mode = newMode;
    }
}