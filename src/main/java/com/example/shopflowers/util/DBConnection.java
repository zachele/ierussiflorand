package com.example.shopflowers.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        String url = getConfig("DB_URL");
        String user = getConfig("DB_USER");
        String password = getConfig("DB_PASSWORD");

        return DriverManager.getConnection(url, user, password);
    }

    private static String getConfig(String key) {
        String value = System.getProperty(key);

        if (value == null || value.isBlank()) {
            value = System.getenv(key);
        }

        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Configurazione mancante: " + key);
        }

        return value;
    }
}