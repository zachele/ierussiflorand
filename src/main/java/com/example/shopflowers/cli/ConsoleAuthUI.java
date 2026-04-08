package com.example.shopflowers.cli;

import com.example.shopflowers.controller.application.LoginController;
import com.example.shopflowers.controller.application.RegisterController;
import com.example.shopflowers.exception.InvalidCredentialsException;
import com.example.shopflowers.exception.UserAlreadyExistsException;
import com.example.shopflowers.model.bean.LoginBean;
import com.example.shopflowers.model.bean.RegisterUserBean;
import com.example.shopflowers.model.entity.User;
import com.example.shopflowers.util.Session;

import java.sql.SQLException;
import java.util.Scanner;

public class ConsoleAuthUI {

    private final Scanner scanner;

    public ConsoleAuthUI(Scanner scanner) {
        this.scanner = scanner;
    }

    public boolean handleLogin() {
        System.out.println();
        System.out.println("=== LOGIN ===");

        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        LoginBean loginBean = new LoginBean();
        loginBean.setUsername(username);
        loginBean.setPassword(password);

        try {
            LoginController loginController = new LoginController();
            User user = loginController.login(loginBean);

            Session.getInstance().setSession(user.getUsername(), user.getRole());

            System.out.println("Login effettuato con successo come " + user.getRole() + ".");
            return true;

        } catch (InvalidCredentialsException e) {
            System.out.println("Errore: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Errore durante il login.");
        } catch (IllegalStateException e) {
            System.out.println("Errore di inizializzazione applicazione: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Errore imprevisto durante il login: " + e.getMessage());
        }

        return false;
    }

    public void handleRegister() {
        System.out.println();
        System.out.println("=== REGISTRAZIONE ===");

        System.out.print("Nome: ");
        String name = scanner.nextLine().trim();

        System.out.print("Cognome: ");
        String surname = scanner.nextLine().trim();

        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        RegisterUserBean registerUserBean = new RegisterUserBean();
        registerUserBean.setName(name);
        registerUserBean.setSurname(surname);
        registerUserBean.setUsername(username);
        registerUserBean.setPassword(password);

        try {
            RegisterController registerController = new RegisterController();
            boolean success = registerController.registerCustomer(registerUserBean);

            if (success) {
                System.out.println("Registrazione completata con successo.");
            } else {
                System.out.println("Registrazione non riuscita. Controlla i dati inseriti.");
            }

        } catch (UserAlreadyExistsException e) {
            System.out.println("Errore: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Errore durante la registrazione.");
        } catch (IllegalStateException e) {
            System.out.println("Errore di inizializzazione applicazione: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Errore imprevisto durante la registrazione: " + e.getMessage());
        }
    }
}