package com.example.shopflowers.model.dao;

import com.example.shopflowers.model.entity.FlowerProduct;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FlowerProductMemoryDAO implements FlowerProductDAO {

    private static final List<FlowerProduct> products = new ArrayList<>();
    private static boolean initialized = false;

    public FlowerProductMemoryDAO() throws SQLException {
        initializeIfNeeded();
    }

    @Override
    public void save(FlowerProduct product) {
        int nextId = getNextId();
        FlowerProduct newProduct = new FlowerProduct(
                nextId,
                product.getName(),
                product.getPrice(),
                product.getColor(),
                product.getVariety(),
                product.getStockQuantity(),
                product.getImageName()
        );
        products.add(newProduct);
    }

    @Override
    public List<FlowerProduct> findAll() {
        return copyProducts(products);
    }

    @Override
    public void update(FlowerProduct product) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == product.getId()) {
                products.set(i, new FlowerProduct(
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        product.getColor(),
                        product.getVariety(),
                        product.getStockQuantity(),
                        product.getImageName()
                ));
                return;
            }
        }
    }

    @Override
    public void deleteById(int id) {
        products.removeIf(product -> product.getId() == id);
    }

    @Override
    public FlowerProduct findById(int id) {
        for (FlowerProduct product : products) {
            if (product.getId() == id) {
                return new FlowerProduct(
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        product.getColor(),
                        product.getVariety(),
                        product.getStockQuantity(),
                        product.getImageName()
                );
            }
        }
        return null;
    }

    @Override
    public void updateStock(int productId, int newStock) {
        for (FlowerProduct product : products) {
            if (product.getId() == productId) {
                product.setStockQuantity(newStock);
                return;
            }
        }
    }

    private void initializeIfNeeded() {
        if (initialized) {
            return;
        }

        products.clear();
        products.add(new FlowerProduct(1, "Rose Rosse", 4.99, "Rosso", "Rosa", 50, "rose_red.png"));
        products.add(new FlowerProduct(2, "Tulipani Gialli", 3.49, "Giallo", "Tulipano", 40, "tulip_yellow.png"));
        products.add(new FlowerProduct(3, "Gigli Bianchi", 5.99, "Bianco", "Giglio", 30, "lily_white.png"));
        products.add(new FlowerProduct(4, "Orchidea Rosa", 12.50, "Rosa", "Orchidea", 15, "orchid_purple.png"));
        products.add(new FlowerProduct(5, "Margherite Miste", 2.99, "Misto", "Margherita", 60, "mixed_bouquet.png"));

        initialized = true;
    }

    private int getNextId() {
        int maxId = 0;

        for (FlowerProduct product : products) {
            if (product.getId() > maxId) {
                maxId = product.getId();
            }
        }

        return maxId + 1;
    }

    private List<FlowerProduct> copyProducts(List<FlowerProduct> source) {
        List<FlowerProduct> copied = new ArrayList<>();

        for (FlowerProduct product : source) {
            copied.add(new FlowerProduct(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    product.getColor(),
                    product.getVariety(),
                    product.getStockQuantity(),
                    product.getImageName()
            ));
        }

        return copied;
    }
}