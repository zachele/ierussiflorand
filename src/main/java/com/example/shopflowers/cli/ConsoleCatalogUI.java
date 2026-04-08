package com.example.shopflowers.cli;

import com.example.shopflowers.controller.application.BrowseCatalogController;
import com.example.shopflowers.model.entity.FlowerProduct;
import com.example.shopflowers.util.Session;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class ConsoleCatalogUI {

    private final Scanner scanner;
    private final BrowseCatalogController browseCatalogController;

    public ConsoleCatalogUI(Scanner scanner) {
        this.scanner = scanner;
        this.browseCatalogController = new BrowseCatalogController();
    }

    public void start() {
        boolean running = true;

        while (running) {
            printCatalogMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> showCatalog();
                case "2" -> showSessionInfo();
                case "3" -> running = false;
                default -> ConsolePrinter.println("Scelta non valida.");
            }
        }
    }

    private void printCatalogMenu() {
        ConsolePrinter.println();
        ConsolePrinter.println("=========== CATALOGO CLI ===========");
        ConsolePrinter.println("1. Visualizza catalogo prodotti");
        ConsolePrinter.println("2. Mostra utente corrente");
        ConsolePrinter.println("3. Torna al menu principale");
        ConsolePrinter.print("Seleziona un'opzione: ");
    }

    private void showCatalog() {
        try {
            List<FlowerProduct> products = browseCatalogController.getAllProducts();

            if (products.isEmpty()) {
                ConsolePrinter.println("Il catalogo è vuoto.");
                return;
            }

            ConsolePrinter.println();
            ConsolePrinter.println("--------------- PRODOTTI ---------------");

            for (FlowerProduct product : products) {
                ConsolePrinter.println(
                        "ID: " + product.getId()
                                + " | Nome: " + product.getName()
                                + " | Prezzo: € " + String.format("%.2f", product.getPrice())
                                + " | Colore: " + product.getColor()
                                + " | Varietà: " + product.getVariety()
                                + " | Stock: " + product.getStockQuantity()
                );
            }

        } catch (SQLException e) {
            ConsolePrinter.println("Errore durante il caricamento del catalogo.");
        } catch (Exception e) {
            ConsolePrinter.println("Errore imprevisto durante la visualizzazione del catalogo.");
        }
    }

    private void showSessionInfo() {
        String username = Session.getInstance().getLoggedUsername();
        String role = Session.getInstance().getLoggedRole();

        if (username == null || role == null) {
            ConsolePrinter.println("Nessuna sessione attiva.");
            return;
        }

        ConsolePrinter.println("Utente corrente: " + username + " | Ruolo: " + role);
    }
}