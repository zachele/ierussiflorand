package com.example.shopflowers.model.dao;

import com.example.shopflowers.model.entity.FlowerProduct;

import java.util.ArrayList;
import java.util.List;

public class FlowerProductMemoryDAO implements FlowerProductDAO {

    private static final List<FlowerProduct> PRODUCTS = new ArrayList<>();
    private static boolean initialized = false;

    public FlowerProductMemoryDAO() {
        initializeIfNeeded();
    }

    @Override
    public void save(FlowerProduct product) {
        int nextId = getNextId();
        PRODUCTS.add(copyProductWithId(product, nextId));
    }

    @Override
    public List<FlowerProduct> findAll() {
        return copyProducts();
    }

    @Override
    public void update(FlowerProduct product) {
        for (int i = 0; i < PRODUCTS.size(); i++) {
            if (PRODUCTS.get(i).getId() == product.getId()) {
                PRODUCTS.set(i, copyProduct(product));
                return;
            }
        }
    }

    @Override
    public void deleteById(int id) {
        PRODUCTS.removeIf(product -> product.getId() == id);
    }

    @Override
    public FlowerProduct findById(int id) {
        for (FlowerProduct product : PRODUCTS) {
            if (product.getId() == id) {
                return copyProduct(product);
            }
        }
        return null;
    }

    @Override
    public void updateStock(int productId, int newStock) {
        for (FlowerProduct product : PRODUCTS) {
            if (product.getId() == productId) {
                product.setStockQuantity(newStock);
                return;
            }
        }
    }

    private static void initializeIfNeeded() {
        if (initialized) {
            return;
        }

        PRODUCTS.clear();
        PRODUCTS.add(new FlowerProduct(1, "Rose Rosse", 4.99, "Rosso", "Rosa", 50, "rose_red.png"));
        PRODUCTS.add(new FlowerProduct(2, "Tulipani Gialli", 3.49, "Giallo", "Tulipano", 40, "tulip_yellow.png"));
        PRODUCTS.add(new FlowerProduct(3, "Gigli Bianchi", 5.99, "Bianco", "Giglio", 30, "lily_white.png"));
        PRODUCTS.add(new FlowerProduct(4, "Orchidea Rosa", 12.50, "Rosa", "Orchidea", 15, "orchid_purple.png"));
        PRODUCTS.add(new FlowerProduct(5, "Margherite Miste", 2.99, "Misto", "Margherita", 60, "mixed_bouquet.png"));

        initialized = true;
    }

    private int getNextId() {
        int maxId = 0;

        for (FlowerProduct product : PRODUCTS) {
            if (product.getId() > maxId) {
                maxId = product.getId();
            }
        }

        return maxId + 1;
    }

    private List<FlowerProduct> copyProducts() {
        List<FlowerProduct> copiedProducts = new ArrayList<>();

        for (FlowerProduct product : PRODUCTS) {
            copiedProducts.add(copyProduct(product));
        }

        return copiedProducts;
    }

    private FlowerProduct copyProduct(FlowerProduct product) {
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

    private FlowerProduct copyProductWithId(FlowerProduct product, int id) {
        return new FlowerProduct(
                id,
                product.getName(),
                product.getPrice(),
                product.getColor(),
                product.getVariety(),
                product.getStockQuantity(),
                product.getImageName()
        );
    }
}