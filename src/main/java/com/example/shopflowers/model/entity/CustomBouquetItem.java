package com.example.shopflowers.model.entity;

public record CustomBouquetItem(
        FlowerProduct flowerProduct,
        int quantity
) {

    public FlowerProduct getFlowerProduct() {
        return flowerProduct;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getProductName() {
        return flowerProduct.getName();
    }

    public double getSubtotal() {
        return flowerProduct.getPrice() * quantity;
    }
    @SuppressWarnings("unused")
    public double getUnitPrice() {
        return flowerProduct.getPrice();
    }
}