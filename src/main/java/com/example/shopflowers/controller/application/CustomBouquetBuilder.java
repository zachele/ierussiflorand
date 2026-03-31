package com.example.shopflowers.controller.application;

import com.example.shopflowers.model.entity.CustomBouquet;
import com.example.shopflowers.model.entity.CustomBouquetItem;

import java.util.ArrayList;
import java.util.List;

public class CustomBouquetBuilder {

    private String size;
    private String packaging;
    private boolean cardIncluded;
    private boolean vaseIncluded;
    private Double maxBudget;
    private final List<CustomBouquetItem> items = new ArrayList<>();

    public void setSize(String size) {
        this.size = size;
    }

    public void setPackaging(String packaging) {
        this.packaging = packaging;
    }

    public void setCardIncluded(boolean cardIncluded) {
        this.cardIncluded = cardIncluded;
    }

    public void setVaseIncluded(boolean vaseIncluded) {
        this.vaseIncluded = vaseIncluded;
    }

    public void setMaxBudget(Double maxBudget) {
        this.maxBudget = maxBudget;
    }

    public Double getMaxBudget() {
        return maxBudget;
    }

    public void addItem(CustomBouquetItem item) {
        for (int i = 0; i < items.size(); i++) {
            CustomBouquetItem existingItem = items.get(i);

            if (existingItem.getFlowerProduct().getId() == item.getFlowerProduct().getId()) {
                int newQuantity = existingItem.getQuantity() + item.getQuantity();

                items.set(i, new CustomBouquetItem(existingItem.getFlowerProduct(), newQuantity));
                return;
            }
        }

        items.add(item);
    }

    public void removeItem(CustomBouquetItem item) {
        items.remove(item);
    }

    public void clearItems() {
        items.clear();
    }

    public List<CustomBouquetItem> getItems() {
        return items;
    }

    public double calculateTotalPrice() {
        double total = 0.0;

        for (CustomBouquetItem item : items) {
            total += item.getSubtotal();
        }

        if ("MEDIO".equalsIgnoreCase(size)) {
            total += 5.0;
        } else if ("GRANDE".equalsIgnoreCase(size)) {
            total += 10.0;
        }
        if ("PREMIUM".equalsIgnoreCase(packaging)) {
            total += 7.0;
        }

        if (cardIncluded) {
            total += 3.0;
        }

        if (vaseIncluded) {
            total += 8.0;
        }

        return total;
    }

    public boolean isWithinBudget() {
        return maxBudget == null || calculateTotalPrice() <= maxBudget;
    }

    public double getExceededAmount() {
        if (maxBudget == null) {
            return 0.0;
        }
        return Math.max(0.0, calculateTotalPrice() - maxBudget);
    }

    public CustomBouquet build() {
        return new CustomBouquet(
                size,
                packaging,
                cardIncluded,
                vaseIncluded,
                new ArrayList<>(items),
                calculateTotalPrice()
        );
    }
}