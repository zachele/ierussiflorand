package com.example.shopflowers.util;

public class Session {

    private static final Session INSTANCE = new Session();

    private String loggedUsername;
    private String loggedRole;

    private Session() {
    }

    public static Session getInstance() {
        return INSTANCE;
    }

    public void setSession(String username, String role) {
        this.loggedUsername = username;
        this.loggedRole = role;
    }

    public String getLoggedUsername() {
        return loggedUsername;
    }

    public String getLoggedRole() {
        return loggedRole;
    }

    public void clearSession() {
        this.loggedUsername = null;
        this.loggedRole = null;
    }
}