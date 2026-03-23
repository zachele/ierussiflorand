package com.example.shopflowers.util;

public class Session {

    private static String loggedUsername;
    private static String loggedRole;

    private Session() {
    }

    public static void setSession(String username, String role) {
        loggedUsername = username;
        loggedRole = role;
    }

    public static String getLoggedUsername() {
        return loggedUsername;
    }

    public static String getLoggedRole() {
        return loggedRole;
    }

    public static void clearSession() {
        loggedUsername = null;
        loggedRole = null;
    }
}