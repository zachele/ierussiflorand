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
    private final ConsoleCheckoutUI consoleCheckoutUI = new ConsoleCheckoutUI(scanner, customerCartController);
    private final ConsoleAdminProductUI consoleAdminProductUI = new ConsoleAdminProductUI(scanner);
    private final ConsoleOperatorOrdersUI consoleOperatorOrdersUI = new ConsoleOperatorOrdersUI(scanner);

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

        if (!loggedIn) {
            return;
        }

        String role = Session.getInstance().getLoggedRole();

        if ("CUSTOMER".equalsIgnoreCase(role)) {
            openCustomerArea();
        } else if ("ADMIN".equalsIgnoreCase(role)) {
            openAdminArea();
        } else if ("OPERATOR".equalsIgnoreCase(role)) {
            openOperatorArea();
        } else {
            ConsolePrinter.println("Ruolo non riconosciuto.");
        }

        Session.getInstance().clearSession();
    }

    private void handleGuestAccess() {
        Session.getInstance().setSession("guest", "GUEST");
        ConsolePrinter.println("Accesso come ospite effettuato.");
        consoleCatalogUI.start();
        Session.getInstance().clearSession();
    }

    private void openCustomerArea() {
        boolean running = true;

        while (running) {
            ConsolePrinter.println();
            ConsolePrinter.println("=========== AREA CUSTOMER ===========");
            ConsolePrinter.println("1. Catalogo");
            ConsolePrinter.println("2. Carrello");
            ConsolePrinter.println("3. Checkout");
            ConsolePrinter.println("4. Logout");
            ConsolePrinter.print("Seleziona un'opzione: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> consoleCatalogUI.start();
                case "2" -> consoleCartUI.start();
                case "3" -> consoleCheckoutUI.start();
                case "4" -> running = false;
                default -> ConsolePrinter.println("Scelta non valida.");
            }
        }
    }

    private void openAdminArea() {
        boolean running = true;

        while (running) {
            ConsolePrinter.println();
            ConsolePrinter.println("============ AREA ADMIN =============");
            ConsolePrinter.println("1. Gestione prodotti");
            ConsolePrinter.println("2. Visualizza catalogo");
            ConsolePrinter.println("3. Logout");
            ConsolePrinter.print("Seleziona un'opzione: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> consoleAdminProductUI.start();
                case "2" -> consoleCatalogUI.start();
                case "3" -> running = false;
                default -> ConsolePrinter.println("Scelta non valida.");
            }
        }
    }

    private void openOperatorArea() {
        boolean running = true;

        while (running) {
            ConsolePrinter.println();
            ConsolePrinter.println("========== AREA OPERATORE ==========");
            ConsolePrinter.println("1. Gestione ordini");
            ConsolePrinter.println("2. Visualizza catalogo");
            ConsolePrinter.println("3. Logout");
            ConsolePrinter.print("Seleziona un'opzione: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> consoleOperatorOrdersUI.start();
                case "2" -> consoleCatalogUI.start();
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