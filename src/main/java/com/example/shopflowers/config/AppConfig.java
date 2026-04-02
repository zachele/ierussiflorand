package com.example.shopflowers.config;

public class AppConfig {

    private static AppMode mode = AppMode.FULL;

    public static AppMode getMode() {
        return mode;
    }

    public static void setMode(AppMode newMode) {
        mode = newMode;
    }
}