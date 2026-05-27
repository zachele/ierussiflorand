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
        ConsolePrinter.println();
        ConsolePrinter.println("=== LOGIN ===");

        ConsolePrinter.print("Username: ");
        String username = scanner.nextLine().trim();

        ConsolePrinter.print("Password: ");
        String password = scanner.nextLine().trim();

        LoginBean loginBean = new LoginBean();
        loginBean.setUsername(username);
        loginBean.setPassword(password);

        try {
            LoginController loginController = new LoginController();
            User user = loginController.login(loginBean);

            Session.getInstance().setSession(
                    user.getUsername(),
                    user.getRole()
            );

            ConsolePrinter.println(
                    "Login effettuato con successo come "
                            + user.getRole()
                            + "."
            );

            return true;

        } catch (InvalidCredentialsException e) {
            ConsolePrinter.println("Errore: " + e.getMessage());
        } catch (SQLException e) {
            ConsolePrinter.println("Errore durante il login.");
        } catch (IllegalStateException e) {
            ConsolePrinter.println("Errore di inizializzazione applicazione: " + e.getMessage());
        } catch (Exception e) {
            ConsolePrinter.println("Errore imprevisto durante il login: " + e.getMessage());
        }

        return false;
    }

    public void handleRegister() {
        ConsolePrinter.println();
        ConsolePrinter.println("=== REGISTRAZIONE ===");

        ConsolePrinter.print("Nome: ");
        String name = scanner.nextLine().trim();

        ConsolePrinter.print("Cognome: ");
        String surname = scanner.nextLine().trim();

        ConsolePrinter.print("Username: ");
        String username = scanner.nextLine().trim();

        ConsolePrinter.print("Password: ");
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
                ConsolePrinter.println("Registrazione completata con successo.");
            } else {
                ConsolePrinter.println("Registrazione non riuscita. Controlla i dati inseriti.");
            }

        } catch (UserAlreadyExistsException e) {
            ConsolePrinter.println("Errore: " + e.getMessage());
        } catch (SQLException e) {
            ConsolePrinter.println("Errore durante la registrazione.");
        } catch (IllegalStateException e) {
            ConsolePrinter.println("Errore di inizializzazione applicazione: " + e.getMessage());
        } catch (Exception e) {
            ConsolePrinter.println("Errore imprevisto durante la registrazione: " + e.getMessage());
        }
    }
}