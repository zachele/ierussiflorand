package com.example.shopflowers.controller.application;

import com.example.shopflowers.model.dao.DAOFactory;
import com.example.shopflowers.model.dao.FlowerProductDAO;
import com.example.shopflowers.model.entity.CustomBouquet;
import com.example.shopflowers.model.entity.CustomBouquetItem;
import com.example.shopflowers.model.entity.FlowerProduct;

import java.sql.SQLException;
import java.util.List;

public class CustomBouquetController {

    private final FlowerProductDAO flowerProductDAO;
    private final CustomBouquetBuilder builder;

    public CustomBouquetController() {
        try {
            this.flowerProductDAO = DAOFactory.getFlowerProductDAO();
        } catch (SQLException e) {
            throw new IllegalStateException("Impossibile inizializzare la DAO dei prodotti.", e);
        }
        this.builder = new CustomBouquetBuilder();
    }

    public List<FlowerProduct> getAvailableFlowers() throws SQLException {
        return flowerProductDAO.findAll();
    }

    public void configureBouquet(String size, String packaging, boolean cardIncluded, boolean vaseIncluded, Double maxBudget) {
        builder.setSize(size);
        builder.setPackaging(packaging);
        builder.setCardIncluded(cardIncluded);
        builder.setVaseIncluded(vaseIncluded);
        builder.setMaxBudget(maxBudget);
    }

    public boolean addFlowerToBouquet(FlowerProduct product, int quantity) {
        if (product == null || quantity <= 0 || quantity > product.getStockQuantity()) {
            return false;
        }

        builder.addItem(new CustomBouquetItem(product, quantity));
        return true;
    }

    public boolean removeFlowerFromBouquet(CustomBouquetItem item) {
        if (item == null) {
            return false;
        }

        builder.removeItem(item);
        return true;
    }

    public List<CustomBouquetItem> getCurrentItems() {
        return builder.getItems();
    }

    public double getCurrentTotal() {
        return builder.calculateTotalPrice();
    }

    public boolean isWithinBudget() {
        return builder.isWithinBudget();
    }

    public double getExceededAmount() {
        return builder.getExceededAmount();
    }

    public CustomBouquet buildBouquet() {
        return builder.build();
    }

    public void resetBouquet() {
        builder.clearItems();
        builder.setSize(null);
        builder.setPackaging(null);
        builder.setCardIncluded(false);
        builder.setVaseIncluded(false);
        builder.setMaxBudget(null);
    }
}