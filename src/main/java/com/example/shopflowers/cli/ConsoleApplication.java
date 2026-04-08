package com.example.shopflowers.cli;

import com.example.shopflowers.util.Session;

import java.util.Scanner;
import com.example.shopflowers.config.AppConfig;
import com.example.shopflowers.config.AppMode;

public class ConsoleApplication {

    private final Scanner scanner = new Scanner(System.in);
    private final ConsoleAuthUI consoleAuthUI = new ConsoleAuthUI(scanner);

    public void start() {
        boolean running = true;
        System.out.println("Modalità attiva: " + AppConfig.getMode().name());
        while (running) {
            printMainMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> handleLogin();
                case "2" -> consoleAuthUI.handleRegister();
                case "3" -> handleGuestAccess();
                case "4" -> running = false;
                default -> System.out.println("Scelta non valida.");
            }
        }

        System.out.println("Chiusura applicazione console.");
    }

    private void printMainMenu() {
        System.out.println();
        System.out.println("====================================");
        System.out.println("         IERUSSI FLOWERS CLI        ");
        System.out.println("====================================");
        System.out.println("1. Login");
        System.out.println("2. Registrazione");
        System.out.println("3. Accesso come ospite");
        System.out.println("4. Esci");
        System.out.print("Seleziona un'opzione: ");
    }

    private void handleLogin() {
        boolean loggedIn = consoleAuthUI.handleLogin();

        if (loggedIn) {
            System.out.println("Area utente CLI in costruzione.");
            Session.getInstance().clearSession();
        }
    }

    private void handleGuestAccess() {
        Session.getInstance().setSession("guest", "GUEST");
        System.out.println("Accesso come ospite effettuato.");
        System.out.println("Catalogo CLI in costruzione.");
        Session.getInstance().clearSession();
    }

    public static void main(String[] args) {
        AppConfig.setMode(AppMode.DEMO);
        new ConsoleApplication().start();
    }
}