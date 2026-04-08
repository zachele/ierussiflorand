package com.example.shopflowers.cli;

import com.example.shopflowers.controller.application.BrowseCatalogController;
import com.example.shopflowers.controller.application.ManageProductsController;
import com.example.shopflowers.model.bean.ProductBean;
import com.example.shopflowers.model.entity.FlowerProduct;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class ConsoleAdminProductUI {

    private final Scanner scanner;
    private final BrowseCatalogController browseCatalogController;
    private final ManageProductsController manageProductsController;

    public ConsoleAdminProductUI(Scanner scanner) {
        this.scanner = scanner;
        this.browseCatalogController = new BrowseCatalogController();
        this.manageProductsController = new ManageProductsController();
    }

    public void start() {
        boolean running = true;

        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> showCatalog();
                case "2" -> handleAddProduct();
                case "3" -> handleUpdateProduct();
                case "4" -> handleDeleteProduct();
                case "5" -> running = false;
                default -> ConsolePrinter.println("Scelta non valida.");
            }
        }
    }

    private void printMenu() {
        ConsolePrinter.println();
        ConsolePrinter.println("======= GESTIONE PRODOTTI ADMIN =======");
        ConsolePrinter.println("1. Visualizza catalogo");
        ConsolePrinter.println("2. Aggiungi prodotto");
        ConsolePrinter.println("3. Modifica prodotto");
        ConsolePrinter.println("4. Elimina prodotto");
        ConsolePrinter.println("5. Torna indietro");
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
        }
    }

    private void handleAddProduct() {
        try {
            ProductBean productBean = buildProductBean(false);
            manageProductsController.addProduct(productBean);
            ConsolePrinter.println("Prodotto aggiunto con successo.");
        } catch (NumberFormatException e) {
            ConsolePrinter.println("Prezzo o stock non validi.");
        } catch (SQLException e) {
            ConsolePrinter.println("Errore durante l'aggiunta del prodotto.");
        }
    }

    private void handleUpdateProduct() {
        try {
            ProductBean productBean = buildProductBean(true);
            manageProductsController.updateProduct(productBean);
            ConsolePrinter.println("Prodotto aggiornato con successo.");
        } catch (NumberFormatException e) {
            ConsolePrinter.println("ID, prezzo o stock non validi.");
        } catch (SQLException e) {
            ConsolePrinter.println("Errore durante la modifica del prodotto.");
        }
    }

    private void handleDeleteProduct() {
        try {
            ConsolePrinter.print("Inserisci ID prodotto da eliminare: ");
            int productId = Integer.parseInt(scanner.nextLine().trim());

            manageProductsController.deleteProductById(productId);
            ConsolePrinter.println("Prodotto eliminato con successo.");

        } catch (NumberFormatException e) {
            ConsolePrinter.println("ID non valido.");
        } catch (SQLException e) {
            ConsolePrinter.println("Errore durante l'eliminazione del prodotto.");
        }
    }

    private ProductBean buildProductBean(boolean includeId) {
        ProductBean productBean = new ProductBean();

        if (includeId) {
            ConsolePrinter.print("ID prodotto: ");
            productBean.setId(Integer.parseInt(scanner.nextLine().trim()));
        }

        ConsolePrinter.print("Nome: ");
        productBean.setName(scanner.nextLine().trim());

        ConsolePrinter.print("Prezzo: ");
        productBean.setPrice(Double.parseDouble(scanner.nextLine().trim()));

        ConsolePrinter.print("Colore: ");
        productBean.setColor(scanner.nextLine().trim());

        ConsolePrinter.print("Varietà: ");
        productBean.setVariety(scanner.nextLine().trim());

        ConsolePrinter.print("Stock: ");
        productBean.setStockQuantity(Integer.parseInt(scanner.nextLine().trim()));

        return productBean;
    }
}