package com.example.shopflowers.model.entity;

public class FlowerProduct {

    private int id;
    private String name;
    private double price;
    private String color;
    private String variety;
    private int stockQuantity;
    private String imageName;

    public FlowerProduct() {
    }

    public FlowerProduct(int id, String name, double price, String color, String variety, int stockQuantity, String imageName) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.color = color;
        this.variety = variety;
        this.stockQuantity = stockQuantity;
        this.imageName = imageName;
    }

    public FlowerProduct(String name, double price, String color, String variety, int stockQuantity, String imageName) {
        this.name = name;
        this.price = price;
        this.color = color;
        this.variety = variety;
        this.stockQuantity = stockQuantity;
        this.imageName = imageName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getVariety() {
        return variety;
    }

    public void setVariety(String variety) {
        this.variety = variety;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    @Override
    public String toString() {
        return "FlowerProduct{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", color='" + color + '\'' +
                ", variety='" + variety + '\'' +
                ", stockQuantity=" + stockQuantity +
                ", imageName='" + imageName + '\'' +
                '}';
    }
}