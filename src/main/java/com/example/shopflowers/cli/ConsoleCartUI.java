package com.example.shopflowers.cli;

import com.example.shopflowers.controller.application.BrowseCatalogController;
import com.example.shopflowers.controller.application.CustomerCartController;
import com.example.shopflowers.exception.EmptyCartException;
import com.example.shopflowers.exception.InvalidQuantityException;
import com.example.shopflowers.exception.ProductNotFoundException;
import com.example.shopflowers.model.entity.CartItem;
import com.example.shopflowers.model.entity.FlowerProduct;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class ConsoleCartUI {

    private static final String INVALID_CHOICE_MESSAGE = "Scelta non valida.";
    private static final String SELECT_OPTION_PROMPT = "Seleziona un'opzione: ";
    private static final String ERROR_PREFIX = "Errore: ";

    private final Scanner scanner;
    private final BrowseCatalogController browseCatalogController;
    private final CustomerCartController customerCartController;

    public ConsoleCartUI(Scanner scanner, CustomerCartController customerCartController) {
        this.scanner = scanner;
        this.customerCartController = customerCartController;
        this.browseCatalogController = new BrowseCatalogController();
    }

    public void start() {
        boolean running = true;

        while (running) {
            printCartMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> showCatalog();
                case "2" -> handleAddToCart();
                case "3" -> showCart();
                case "4" -> handleRemoveFromCart();
                case "5" -> handleClearCart();
                case "6" -> running = false;
                default -> ConsolePrinter.println(INVALID_CHOICE_MESSAGE);
            }
        }
    }

    private void printCartMenu() {
        ConsolePrinter.println();
        ConsolePrinter.println("============= CARRELLO CLI =============");
        ConsolePrinter.println("1. Visualizza catalogo");
        ConsolePrinter.println("2. Aggiungi prodotto al carrello");
        ConsolePrinter.println("3. Visualizza carrello");
        ConsolePrinter.println("4. Rimuovi prodotto dal carrello");
        ConsolePrinter.println("5. Svuota carrello");
        ConsolePrinter.println("6. Torna indietro");
        ConsolePrinter.print(SELECT_OPTION_PROMPT);
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
            ConsolePrinter.printProducts(products);

        } catch (SQLException e) {
            ConsolePrinter.println("Errore durante il caricamento del catalogo.");
        }
    }

    private void handleAddToCart() {
        try {
            ConsolePrinter.print("Inserisci ID prodotto: ");
            int productId = Integer.parseInt(scanner.nextLine().trim());

            ConsolePrinter.print("Inserisci quantità: ");
            int quantity = Integer.parseInt(scanner.nextLine().trim());

            FlowerProduct selectedProduct = findProductById(productId);

            if (selectedProduct == null) {
                ConsolePrinter.println("Prodotto non trovato.");
                return;
            }

            boolean added = customerCartController.addToCart(selectedProduct, quantity);

            if (!added) {
                ConsolePrinter.println("Operazione non riuscita. Quantità richiesta non disponibile.");
                return;
            }

            ConsolePrinter.println("Prodotto aggiunto al carrello con successo.");

        } catch (NumberFormatException e) {
            ConsolePrinter.println("Inserisci valori numerici validi.");
        } catch (InvalidQuantityException e) {
            ConsolePrinter.println(ERROR_PREFIX + e.getMessage());
        } catch (SQLException e) {
            ConsolePrinter.println("Errore durante la ricerca del prodotto.");
        }
    }

    private void showCart() {
        List<CartItem> items = customerCartController.getCartItems();

        if (items.isEmpty()) {
            ConsolePrinter.println("Il carrello è vuoto.");
            return;
        }

        ConsolePrinter.println();
        ConsolePrinter.println("--------------- CARRELLO ---------------");

        for (CartItem item : items) {
            ConsolePrinter.println(
                    "ID: " + item.getProduct().getId()
                            + " | Nome: " + item.getProductName()
                            + " | Quantità: " + item.getQuantity()
                            + " | Totale: € " + String.format("%.2f", item.getTotalPrice())
            );
        }

        try {
            ConsolePrinter.println("Totale carrello: € " + String.format("%.2f", customerCartController.getCartTotal()));
        } catch (EmptyCartException e) {
            ConsolePrinter.println("Totale carrello: € 0.00");
        }
    }

    private void handleRemoveFromCart() {
        try {
            ConsolePrinter.print("Inserisci ID prodotto da rimuovere: ");
            int productId = Integer.parseInt(scanner.nextLine().trim());

            customerCartController.removeFromCart(productId);
            ConsolePrinter.println("Prodotto rimosso dal carrello.");

        } catch (NumberFormatException e) {
            ConsolePrinter.println("Inserisci un ID valido.");
        } catch (ProductNotFoundException e) {
            ConsolePrinter.println(ERROR_PREFIX + e.getMessage());
        }
    }

    private void handleClearCart() {
        try {
            customerCartController.clearCart();
            ConsolePrinter.println("Carrello svuotato con successo.");
        } catch (EmptyCartException e) {
            ConsolePrinter.println(ERROR_PREFIX + e.getMessage());
        }
    }

    private FlowerProduct findProductById(int productId) throws SQLException {
        List<FlowerProduct> products = browseCatalogController.getAllProducts();

        for (FlowerProduct product : products) {
            if (product.getId() == productId) {
                return product;
            }
        }

        return null;
    }
}