package com.example.shopflowers;

import com.example.shopflowers.controller.application.BrowseCatalogController;
import com.example.shopflowers.controller.application.ManageProductsController;
import com.example.shopflowers.model.entity.FlowerProduct;

import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        BrowseCatalogController browseController = new BrowseCatalogController();
        ManageProductsController manageController = new ManageProductsController();

        try {
            // AGGIUNTA PRODOTTO
            FlowerProduct orchid = new FlowerProduct("Orchidea Viola", 18.90, "Viola", "Phalaenopsis", 8);
            manageController.addProduct(orchid);
            System.out.println("Prodotto aggiunto.");

            // LETTURA CATALOGO
            System.out.println("\n=== Catalogo prodotti ===");
            List<FlowerProduct> products = browseController.getAllProducts();
            for (FlowerProduct product : products) {
                System.out.println(product);
            }

            // LETTURA PRODOTTO SPECIFICO
            FlowerProduct product = browseController.getProductById(1);
            if (product != null) {
                System.out.println("\n=== Prodotto con id 1 ===");
                System.out.println(product);

                // AGGIORNAMENTO
                product.setPrice(14.99);
                product.setStockQuantity(40);
                manageController.updateProduct(product);

                System.out.println("\n=== Prodotto aggiornato ===");
                System.out.println(browseController.getProductById(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}