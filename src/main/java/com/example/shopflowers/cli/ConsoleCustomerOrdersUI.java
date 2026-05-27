package com.example.shopflowers.cli;

import com.example.shopflowers.controller.application.CustomerOrdersController;
import com.example.shopflowers.model.entity.OrderItemSummary;
import com.example.shopflowers.model.entity.OrderSummary;
import com.example.shopflowers.util.Session;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class ConsoleCustomerOrdersUI {

    private final Scanner scanner;
    private final CustomerOrdersController customerOrdersController;

    public ConsoleCustomerOrdersUI(Scanner scanner) {
        this.scanner = scanner;
        this.customerOrdersController = new CustomerOrdersController();
    }

    public void start() {
        boolean running = true;

        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> showMyOrders();
                case "2" -> showOrderDetails();
                case "3" -> running = false;
                default -> ConsolePrinter.println("Scelta non valida.");
            }
        }
    }

    private void printMenu() {
        ConsolePrinter.println();
        ConsolePrinter.println("========== I MIEI ORDINI ==========");
        ConsolePrinter.println("1. Visualizza i miei ordini");
        ConsolePrinter.println("2. Visualizza dettaglio ordine");
        ConsolePrinter.println("3. Torna indietro");
        ConsolePrinter.print("Seleziona un'opzione: ");
    }

    private void showMyOrders() {

        String username =
                Session.getInstance().getLoggedUsername();

        if (username == null || username.isBlank()) {
            ConsolePrinter.println("Nessun utente autenticato.");
            return;
        }

        try {
            List<OrderSummary> orders = customerOrdersController.getOrdersByUsername(username);

            ConsolePrinter.println();
            ConsolePrinter.println("------------ ORDINI UTENTE ------------");

            if (orders.isEmpty()) {
                ConsolePrinter.println("Nessun ordine trovato.");
                return;
            }

            for (OrderSummary order : orders) {
                ConsolePrinter.println(
                        "ID: " + order.getId()
                                + " | Stato: " + order.getStatus()
                                + " | Totale: € " + String.format("%.2f", order.getTotal())
                                + " | Data: " + order.getOrderDate()
                                + " | Consegna: " + order.getDeliveryMode()
                );
            }

        } catch (SQLException e) {
            ConsolePrinter.println("Errore durante il caricamento degli ordini.");
        }
    }

    private void showOrderDetails() {
        try {
            ConsolePrinter.print("Inserisci ID ordine: ");
            int orderId = Integer.parseInt(scanner.nextLine().trim());

            List<OrderItemSummary> items = customerOrdersController.getItemsByOrderId(orderId);

            ConsolePrinter.println();
            ConsolePrinter.println("---------- DETTAGLIO ORDINE ----------");

            if (items.isEmpty()) {
                ConsolePrinter.println("Nessun dettaglio trovato per questo ordine.");
                return;
            }

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
}