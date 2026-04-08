package com.example.shopflowers.cli;

import com.example.shopflowers.config.AppConfig;
import com.example.shopflowers.config.AppMode;
import com.example.shopflowers.controller.application.CustomerCartController;
import com.example.shopflowers.util.Session;

import java.util.Scanner;

public class ConsoleApplication {

    private final Scanner scanner = new Scanner(System.in);
    private final ConsoleAuthUI consoleAuthUI = new ConsoleAuthUI(scanner);
    private final ConsoleCatalogUI consoleCatalogUI = new ConsoleCatalogUI(scanner);
    private final CustomerCartController customerCartController = new CustomerCartController();
    private final ConsoleCartUI consoleCartUI = new ConsoleCartUI(scanner, customerCartController);

    public void start() {
        boolean running = true;

        while (running) {
            printMainMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> handleLogin();
                case "2" -> consoleAuthUI.handleRegister();
                case "3" -> handleGuestAccess();
                case "4" -> running = false;
                default -> ConsolePrinter.println("Scelta non valida.");
            }
        }

        ConsolePrinter.println("Chiusura applicazione console.");
    }

    private void printMainMenu() {
        ConsolePrinter.println();
        ConsolePrinter.println("====================================");
        ConsolePrinter.println("         IERUSSI FLOWERS CLI        ");
        ConsolePrinter.println("====================================");
        ConsolePrinter.println("Modalità attiva: " + AppConfig.getMode().name());
        ConsolePrinter.println("1. Login");
        ConsolePrinter.println("2. Registrazione");
        ConsolePrinter.println("3. Accesso come ospite");
        ConsolePrinter.println("4. Esci");
        ConsolePrinter.print("Seleziona un'opzione: ");
    }

    private void handleLogin() {
        boolean loggedIn = consoleAuthUI.handleLogin();

        if (loggedIn) {
            openUserArea();
            Session.getInstance().clearSession();
        }
    }

    private void handleGuestAccess() {
        Session.getInstance().setSession("guest", "GUEST");
        ConsolePrinter.println("Accesso come ospite effettuato.");
        consoleCatalogUI.start();
        Session.getInstance().clearSession();
    }

    private void openUserArea() {
        boolean running = true;

        while (running) {
            ConsolePrinter.println();
            ConsolePrinter.println("============= AREA UTENTE =============");
            ConsolePrinter.println("1. Catalogo");
            ConsolePrinter.println("2. Operazioni Carrello");
            ConsolePrinter.println("3. Logout");
            ConsolePrinter.print("Seleziona un'opzione: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> consoleCatalogUI.start();
                case "2" -> consoleCartUI.start();
                case "3" -> running = false;
                default -> ConsolePrinter.println("Scelta non valida.");
            }
        }
    }

    public static void main(String[] args) {
        AppConfig.setMode(AppMode.DEMO);
        new ConsoleApplication().start();
    }
}