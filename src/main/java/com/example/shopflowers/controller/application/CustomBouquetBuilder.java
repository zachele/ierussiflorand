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

    public void addItem(CustomBouquetItem item) {
        items.add(item);
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

        total += switch (size) {
            case "MEDIO" -> 5.0;
            case "GRANDE" -> 10.0;
            default -> 0.0;
        };

        total += switch (packaging) {
            case "PREMIUM" -> 7.0;
            default -> 0.0;
        };

        if (cardIncluded) {
            total += 3.0;
        }

        if (vaseIncluded) {
            total += 8.0;
        }

        return total;
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
