package com.example.shopflowers.controller.application;

import com.example.shopflowers.model.dao.FlowerProductDAO;
import com.example.shopflowers.model.entity.FlowerProduct;

import java.sql.SQLException;
import java.util.List;

public class BrowseCatalogController {

    private final FlowerProductDAO flowerProductDAO;

    public BrowseCatalogController() {
        this.flowerProductDAO = new FlowerProductDAO();
    }

    public List<FlowerProduct> getAllProducts() throws SQLException {
        return flowerProductDAO.findAll();
    }

    public FlowerProduct getProductById(int id) throws SQLException {
        return flowerProductDAO.findById(id);
    }
}