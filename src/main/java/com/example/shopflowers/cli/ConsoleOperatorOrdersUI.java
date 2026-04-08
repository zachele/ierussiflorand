package com.example.shopflowers.cli;

import com.example.shopflowers.controller.application.OperatorOrdersController;
import com.example.shopflowers.model.entity.OrderItemSummary;
import com.example.shopflowers.model.entity.OrderSummary;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class ConsoleOperatorOrdersUI {

    private final Scanner scanner;
    private final OperatorOrdersController operatorOrdersController;

    public ConsoleOperatorOrdersUI(Scanner scanner) {
        this.scanner = scanner;
        this.operatorOrdersController = new OperatorOrdersController();
    }

    public void start() {
        boolean running = true;

        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> showActiveOrders();
                case "2" -> showCompletedOrders();
                case "3" -> showOrderDetails();
                case "4" -> updateOrderStatus();
                case "5" -> running = false;
                default -> ConsolePrinter.println("Scelta non valida.");
            }
        }
    }

    private void printMenu() {
        ConsolePrinter.println();
        ConsolePrinter.println("======= GESTIONE ORDINI OPERATORE =======");
        ConsolePrinter.println("1. Visualizza ordini attivi");
        ConsolePrinter.println("2. Visualizza ordini completati");
        ConsolePrinter.println("3. Visualizza dettagli ordine");
        ConsolePrinter.println("4. Aggiorna stato ordine");
        ConsolePrinter.println("5. Torna indietro");
        ConsolePrinter.print("Seleziona un'opzione: ");
    }

    private void showActiveOrders() {
        try {
            List<OrderSummary> orders = operatorOrdersController.getActiveOrders();
            printOrders(orders, "ORDINI ATTIVI");
        } catch (SQLException e) {
            ConsolePrinter.println("Errore durante il caricamento degli ordini attivi.");
        }
    }

    private void showCompletedOrders() {
        try {
            List<OrderSummary> orders = operatorOrdersController.getCompletedOrders();
            printOrders(orders, "ORDINI COMPLETATI");
        } catch (SQLException e) {
            ConsolePrinter.println("Errore durante il caricamento degli ordini completati.");
        }
    }

    private void showOrderDetails() {
        try {
            ConsolePrinter.print("Inserisci ID ordine: ");
            int orderId = Integer.parseInt(scanner.nextLine().trim());

            List<OrderItemSummary> items = operatorOrdersController.getItemsByOrderId(orderId);

            if (items.isEmpty()) {
                ConsolePrinter.println("Nessun dettaglio trovato per questo ordine.");
                return;
            }

            ConsolePrinter.println();
            ConsolePrinter.println("----------- DETTAGLI ORDINE -----------");

            for (OrderItemSummary item : items) {
                ConsolePrinter.println(
                        "Prodotto: " + item.getProductName()
                                + " | Quantità: " + item.getQuantity()
                                + " | Prezzo unitario: € " + String.format("%.2f", item.getUnitPrice())
                );
            }

        } catch (NumberFormatException e) {
            ConsolePrinter.println("ID ordine non valido.");
        } catch (SQLException e) {
            ConsolePrinter.println("Errore durante il caricamento del dettaglio ordine.");
        }
    }

    private void updateOrderStatus() {
        try {
            ConsolePrinter.print("Inserisci ID ordine: ");
            int orderId = Integer.parseInt(scanner.nextLine().trim());

            ConsolePrinter.println("Stati disponibili:");
            ConsolePrinter.println("1. IN_PREPARAZIONE");
            ConsolePrinter.println("2. PRONTO");
            ConsolePrinter.println("3. CONSEGNATO");
            ConsolePrinter.print("Seleziona nuovo stato: ");

            String statusChoice = scanner.nextLine().trim();
            String newStatus = mapStatusChoice(statusChoice);

            if (newStatus == null) {
                ConsolePrinter.println("Stato non valido.");
                return;
            }

            operatorOrdersController.updateOrderStatus(orderId, newStatus);
            ConsolePrinter.println("Stato ordine aggiornato con successo.");

        } catch (NumberFormatException e) {
            ConsolePrinter.println("ID ordine non valido.");
        } catch (SQLException e) {
            ConsolePrinter.println("Errore durante l'aggiornamento dello stato ordine.");
        }
    }

    private String mapStatusChoice(String choice) {
        return switch (choice) {
            case "1" -> "IN_PREPARAZIONE";
            case "2" -> "PRONTO";
            case "3" -> "CONSEGNATO";
            default -> null;
        };
    }

    private void printOrders(List<OrderSummary> orders, String title) {
        ConsolePrinter.println();
        ConsolePrinter.println("----------- " + title + " -----------");

        if (orders.isEmpty()) {
            ConsolePrinter.println("Nessun ordine disponibile.");
            return;
        }

        for (OrderSummary order : orders) {
            ConsolePrinter.println(
                    "ID: " + order.getId()
                            + " | Utente: " + order.getUsername()
                            + " | Stato: " + order.getStatus()
                            + " | Totale: € " + String.format("%.2f", order.getTotal())
                            + " | Data: " + order.getOrderDate()
            );
        }
    }
}