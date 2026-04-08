package com.example.shopflowers.cli;

import com.example.shopflowers.config.AppConfig;
import com.example.shopflowers.config.AppMode;
import com.example.shopflowers.controller.application.CustomerCartController;
import com.example.shopflowers.util.Session;

import java.util.Scanner;

public class ConsoleApplication {

    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        boolean running = true;

        while (running) {
            printMainMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> handleModeSelection();
                case "2" -> handleLogin();
                case "3" -> handleRegister();
                case "4" -> handleGuestAccess();
                case "5" -> running = false;
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
        ConsolePrinter.println("1. Cambia modalità");
        ConsolePrinter.println("2. Login");
        ConsolePrinter.println("3. Registrazione");
        ConsolePrinter.println("4. Accesso come ospite");
        ConsolePrinter.println("5. Esci");
        ConsolePrinter.print("Seleziona un'opzione: ");
    }

    private void handleModeSelection() {
        ConsolePrinter.println();
        ConsolePrinter.println("Seleziona modalità:");
        ConsolePrinter.println("1. DEMO");
        ConsolePrinter.println("2. FILE");
        ConsolePrinter.println("3. FULL");
        ConsolePrinter.print("Scelta: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1" -> applyMode(AppMode.DEMO);
            case "2" -> applyMode(AppMode.FILE);
            case "3" -> applyMode(AppMode.FULL);
            default -> ConsolePrinter.println("Modalità non valida.");
        }
    }

    private void applyMode(AppMode mode) {
        AppConfig.setMode(mode);
        Session.getInstance().clearSession();
        ConsolePrinter.println("Modalità applicata: " + mode.name());
    }

    private void handleLogin() {
        ConsoleAuthUI consoleAuthUI = new ConsoleAuthUI(scanner);
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

    private void handleRegister() {
        ConsoleAuthUI consoleAuthUI = new ConsoleAuthUI(scanner);
        consoleAuthUI.handleRegister();
    }

    private void handleGuestAccess() {
        Session.getInstance().setSession("guest", "GUEST");
        ConsolePrinter.println("Accesso come ospite effettuato.");

        ConsoleCatalogUI consoleCatalogUI = new ConsoleCatalogUI(scanner);
        consoleCatalogUI.start();

        Session.getInstance().clearSession();
    }

    private void openCustomerArea() {
        boolean running = true;
        CustomerCartController customerCartController = new CustomerCartController();

        while (running) {
            ConsolePrinter.println();
            ConsolePrinter.println("=========== AREA CUSTOMER ===========");
            ConsolePrinter.println("1. Catalogo");
            ConsolePrinter.println("2. Carrello");
            ConsolePrinter.println("3. Checkout");
            ConsolePrinter.println("4. I miei ordini");
            ConsolePrinter.println("5. Logout");
            ConsolePrinter.print("Seleziona un'opzione: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    ConsoleCatalogUI consoleCatalogUI = new ConsoleCatalogUI(scanner);
                    consoleCatalogUI.start();
                }
                case "2" -> {
                    ConsoleCartUI consoleCartUI = new ConsoleCartUI(scanner, customerCartController);
                    consoleCartUI.start();
                }
                case "3" -> {
                    ConsoleCheckoutUI consoleCheckoutUI = new ConsoleCheckoutUI(scanner, customerCartController);
                    consoleCheckoutUI.start();
                }
                case "4" -> {
                    ConsoleCustomerOrdersUI consoleCustomerOrdersUI = new ConsoleCustomerOrdersUI(scanner);
                    consoleCustomerOrdersUI.start();
                }
                case "5" -> running = false;
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
                case "1" -> {
                    ConsoleAdminProductUI consoleAdminProductUI = new ConsoleAdminProductUI(scanner);
                    consoleAdminProductUI.start();
                }
                case "2" -> {
                    ConsoleCatalogUI consoleCatalogUI = new ConsoleCatalogUI(scanner);
                    consoleCatalogUI.start();
                }
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
                case "1" -> {
                    ConsoleOperatorOrdersUI consoleOperatorOrdersUI = new ConsoleOperatorOrdersUI(scanner);
                    consoleOperatorOrdersUI.start();
                }
                case "2" -> {
                    ConsoleCatalogUI consoleCatalogUI = new ConsoleCatalogUI(scanner);
                    consoleCatalogUI.start();
                }
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