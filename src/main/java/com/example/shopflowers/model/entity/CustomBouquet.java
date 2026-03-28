package com.example.shopflowers.model.entity;

import java.util.List;

public class CustomBouquet {

    private final String size;
    private final String packaging;
    private final boolean cardIncluded;
    private final boolean vaseIncluded;
    private final List<CustomBouquetItem> items;
    private final double totalPrice;

    public CustomBouquet(String size, String packaging, boolean cardIncluded, boolean vaseIncluded,
                         List<CustomBouquetItem> items, double totalPrice) {
        this.size = size;
        this.packaging = packaging;
        this.cardIncluded = cardIncluded;
        this.vaseIncluded = vaseIncluded;
        this.items = items;
        this.totalPrice = totalPrice;
    }

    public String getSize() {
        return size;
    }

    public String getPackaging() {
        return packaging;
    }

    public boolean isCardIncluded() {
        return cardIncluded;
    }

    public boolean isVaseIncluded() {
        return vaseIncluded;
    }

    public List<CustomBouquetItem> getItems() {
        return items;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getDescription() {
        StringBuilder description = new StringBuilder("Bouquet ");
        description.append(size).append(" - ").append(packaging);

        if (cardIncluded) {
            description.append(" + biglietto");
        }
        if (vaseIncluded) {
            description.append(" + vaso");
        }

        return description.toString();
    }
}