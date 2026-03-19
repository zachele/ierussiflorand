package com.example.shopflowers.controller.application;

import com.example.shopflowers.model.dao.FlowerProductDAO;
import com.example.shopflowers.model.entity.FlowerProduct;

import java.sql.SQLException;

public class ManageProductsController {

    private final FlowerProductDAO flowerProductDAO;

    public ManageProductsController() {
        this.flowerProductDAO = new FlowerProductDAO();
    }

    public void addProduct(FlowerProduct product) throws SQLException {
        flowerProductDAO.save(product);
    }

    public void updateProduct(FlowerProduct product) throws SQLException {
        flowerProductDAO.update(product);
    }

    public void deleteProductById(int id) throws SQLException {
        flowerProductDAO.deleteById(id);
    }
}