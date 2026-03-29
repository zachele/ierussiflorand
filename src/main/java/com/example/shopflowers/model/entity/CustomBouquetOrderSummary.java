package com.example.shopflowers.model.entity;

public class CustomBouquetOrderSummary {

    private final String size;
    private final String packaging;
    private final boolean cardIncluded;
    private final boolean vaseIncluded;
    private final double totalPrice;

    public CustomBouquetOrderSummary(String size, String packaging,
                                     boolean cardIncluded, boolean vaseIncluded,
                                     double totalPrice) {
        this.size = size;
        this.packaging = packaging;
        this.cardIncluded = cardIncluded;
        this.vaseIncluded = vaseIncluded;
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

    public double getTotalPrice() {
        return totalPrice;
    }
}