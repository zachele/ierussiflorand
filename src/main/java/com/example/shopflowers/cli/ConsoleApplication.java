package com.example.shopflowers.cli;

import com.example.shopflowers.config.AppConfig;
import com.example.shopflowers.config.AppMode;
import com.example.shopflowers.controller.application.CustomerCartController;
import com.example.shopflowers.util.Session;

import java.util.Scanner;

public class ConsoleApplication {

    private static final String INVALID_CHOICE_MESSAGE = "Scelta non valida.";
    private static final String SELECT_OPTION_PROMPT = "Seleziona un'opzione: ";
    private static final String MODE_SELECTION_PROMPT = "Scelta: ";

    private static final String ROLE_CUSTOMER = "CUSTOMER";
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_OPERATOR = "OPERATOR";
    private static final String ROLE_GUEST = "GUEST";

    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        boolean running = true;

        while (running) {
            printMainMenu();
            String choice = readChoice();

            switch (choice) {
                case "1" -> handleModeSelection();
                case "2" -> handleLogin();
                case "3" -> handleRegister();
                case "4" -> handleGuestAccess();
                case "5" -> running = false;
                default -> ConsolePrinter.println(INVALID_CHOICE_MESSAGE);
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
        ConsolePrinter.print(SELECT_OPTION_PROMPT);
    }

    private void handleModeSelection() {
        ConsolePrinter.println();
        ConsolePrinter.println("Seleziona modalità:");
        ConsolePrinter.println("1. DEMO");
        ConsolePrinter.println("2. FILE");
        ConsolePrinter.println("3. FULL");
        ConsolePrinter.print(MODE_SELECTION_PROMPT);

        String choice = readChoice();

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

        openAreaByRole(Session.getInstance().getLoggedRole());
        Session.getInstance().clearSession();
    }

    private void openAreaByRole(String role) {
        if (ROLE_CUSTOMER.equalsIgnoreCase(role)) {
            openCustomerArea();
            return;
        }

        if (ROLE_ADMIN.equalsIgnoreCase(role)) {
            openAdminArea();
            return;
        }

        if (ROLE_OPERATOR.equalsIgnoreCase(role)) {
            openOperatorArea();
            return;
        }

        ConsolePrinter.println("Ruolo non riconosciuto.");
    }

    private void handleRegister() {
        ConsoleAuthUI consoleAuthUI = new ConsoleAuthUI(scanner);
        consoleAuthUI.handleRegister();
    }

    private void handleGuestAccess() {
        Session.getInstance().setSession("guest", ROLE_GUEST);
        ConsolePrinter.println("Accesso come ospite effettuato.");

        new ConsoleCatalogUI(scanner).start();

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
            ConsolePrinter.print(SELECT_OPTION_PROMPT);

            String choice = readChoice();

            switch (choice) {
                case "1" -> new ConsoleCatalogUI(scanner).start();
                case "2" -> new ConsoleCartUI(scanner, customerCartController).start();
                case "3" -> new ConsoleCheckoutUI(scanner, customerCartController).start();
                case "4" -> new ConsoleCustomerOrdersUI(scanner).start();
                case "5" -> running = false;
                default -> ConsolePrinter.println(INVALID_CHOICE_MESSAGE);
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
            ConsolePrinter.print(SELECT_OPTION_PROMPT);

            String choice = readChoice();

            switch (choice) {
                case "1" -> new ConsoleAdminProductUI(scanner).start();
                case "2" -> new ConsoleCatalogUI(scanner).start();
                case "3" -> running = false;
                default -> ConsolePrinter.println(INVALID_CHOICE_MESSAGE);
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
            ConsolePrinter.print(SELECT_OPTION_PROMPT);

            String choice = readChoice();

            switch (choice) {
                case "1" -> new ConsoleOperatorOrdersUI(scanner).start();
                case "2" -> new ConsoleCatalogUI(scanner).start();
                case "3" -> running = false;
                default -> ConsolePrinter.println(INVALID_CHOICE_MESSAGE);
            }
        }
    }

    private String readChoice() {
        return scanner.nextLine().trim();
    }

    public static void main(String[] args) {
        AppConfig.setMode(AppMode.DEMO);
        new ConsoleApplication().start();
    }
}