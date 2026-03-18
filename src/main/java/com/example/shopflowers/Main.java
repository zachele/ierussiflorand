package com.example.shopflowers;

import com.example.shopflowers.util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try (Connection connection = DBConnection.getConnection()) {
            System.out.println("Connessione al database riuscita.");
        } catch (SQLException e) {
            System.out.println("Errore di connessione al database.");
            e.printStackTrace();
        }
    }
}
