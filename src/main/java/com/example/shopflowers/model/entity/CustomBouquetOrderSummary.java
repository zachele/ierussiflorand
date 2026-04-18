package com.example.shopflowers.model.entity;

public record CustomBouquetOrderSummary(
        String size,
        String packaging,
        boolean cardIncluded,
        boolean vaseIncluded,
        double totalPrice
) {

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

    public double getTotalPrice() {
        return totalPrice;
    }
}