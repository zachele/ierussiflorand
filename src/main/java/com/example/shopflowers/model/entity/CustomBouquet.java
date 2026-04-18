package com.example.shopflowers.model.entity;

import java.util.List;

public record CustomBouquet(
        String size,
        String packaging,
        boolean cardIncluded,
        boolean vaseIncluded,
        List<CustomBouquetItem> items,
        double totalPrice
) {

    public CustomBouquet {
        items = items == null ? List.of() : List.copyOf(items);
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