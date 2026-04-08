package com.example.shopflowers.cli;

import com.example.shopflowers.config.AppConfig;
import com.example.shopflowers.config.AppMode;
import com.example.shopflowers.util.Session;

import java.util.Scanner;

public class ConsoleApplication {

    private final Scanner scanner = new Scanner(System.in);
    private final ConsoleAuthUI consoleAuthUI = new ConsoleAuthUI(scanner);

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
            ConsolePrinter.println("Area utente CLI in costruzione.");
            Session.getInstance().clearSession();
        }
    }

    private void handleGuestAccess() {
        Session.getInstance().setSession("guest", "GUEST");
        ConsolePrinter.println("Accesso come ospite effettuato.");
        ConsolePrinter.println("Catalogo CLI in costruzione.");
        Session.getInstance().clearSession();
    }

    public static void main(String[] args) {
        AppConfig.setMode(AppMode.DEMO);
        new ConsoleApplication().start();
    }
}