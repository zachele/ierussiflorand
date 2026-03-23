package com.example.shopflowers;

import com.example.shopflowers.controller.application.LoginController;
import com.example.shopflowers.model.entity.User;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        LoginController loginController = new LoginController();

        try {
            User user = loginController.login("admin", "admin123");

            if (user != null) {
                System.out.println("Login riuscito.");
                System.out.println("Username: " + user.getUsername());
                System.out.println("Ruolo: " + user.getRole());
            } else {
                System.out.println("Credenziali non valide.");
            }

        } catch (SQLException e) {
        }
    }
}