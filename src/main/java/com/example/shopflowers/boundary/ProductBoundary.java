package com.example.shopflowers.boundary;

import com.example.shopflowers.controller.application.ManageProductsController;
import com.example.shopflowers.model.bean.ProductBean;

import java.sql.SQLException;

public class ProductBoundary {

    private final ManageProductsController manageProductsController;

    public ProductBoundary() {
        this.manageProductsController = new ManageProductsController();
    }

    public void addProduct(
            String name,
            double price,
            String color,
            String variety,
            int stockQuantity
    ) throws SQLException {

        ProductBean productBean = new ProductBean();
        productBean.setName(name);
        productBean.setPrice(price);
        productBean.setColor(color);
        productBean.setVariety(variety);
        productBean.setStockQuantity(stockQuantity);

        manageProductsController.addProduct(productBean);
    }

    public void updateProduct(
            int id,
            String name,
            double price,
            String color,
            String variety,
            int stockQuantity
    ) throws SQLException {

        ProductBean productBean = new ProductBean();
        productBean.setId(id);
        productBean.setName(name);
        productBean.setPrice(price);
        productBean.setColor(color);
        productBean.setVariety(variety);
        productBean.setStockQuantity(stockQuantity);

        manageProductsController.updateProduct(productBean);
    }

    public void deleteProduct(int id) throws SQLException {
        manageProductsController.deleteProductById(id);
    }
}