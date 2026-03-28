package com.example.shopflowers.model.entity;

public class CustomBouquetItem {

    private final FlowerProduct flowerProduct;
    private final int quantity;

    public CustomBouquetItem(FlowerProduct flowerProduct, int quantity) {
        this.flowerProduct = flowerProduct;
        this.quantity = quantity;
    }

    public FlowerProduct getFlowerProduct() {
        return flowerProduct;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getProductName() {
        return flowerProduct.getName();
    }

    public double getUnitPrice() {
        return flowerProduct.getPrice();
    }

    public double getSubtotal() {
        return flowerProduct.getPrice() * quantity;
    }
}