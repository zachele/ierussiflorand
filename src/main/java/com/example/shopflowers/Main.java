package com.example.shopflowers;

import com.example.shopflowers.model.dao.FlowerProductDAO;
import com.example.shopflowers.model.entity.FlowerProduct;

import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        FlowerProductDAO dao = new FlowerProductDAO();

        try {
            // INSERT
            FlowerProduct lily = new FlowerProduct("Giglio Bianco", 11.50, "Bianco", "Oriental", 12);
            dao.save(lily);
            System.out.println("=== Prodotto inserito ===");
            System.out.println("Inserito Giglio Bianco");

            // SELECT ALL
            System.out.println("\n=== Tutti i prodotti ===");
            List<FlowerProduct> products = dao.findAll();
            for (FlowerProduct product : products) {
                System.out.println(product);
            }

            // FIND BY ID
            FlowerProduct product = dao.findById(2);
            if (product != null) {
                System.out.println("\n=== Prodotto con id 2 ===");
                System.out.println(product);

                // UPDATE
                product.setPrice(13.49);
                product.setStockQuantity(25);
                dao.update(product);

                System.out.println("\n=== Prodotto aggiornato ===");
                System.out.println(dao.findById(1));

                // DELETE
                dao.deleteById(product.getId());
                System.out.println("\n=== Prodotto eliminato ===");
                System.out.println("Eliminato prodotto con id " + product.getId());
            } else {
                System.out.println("\nNessun prodotto trovato con id 1.");
            }

            // SELECT ALL finale
            System.out.println("\n=== Prodotti finali ===");
            List<FlowerProduct> finalProducts = dao.findAll();
            for (FlowerProduct p : finalProducts) {
                System.out.println(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}