package com.example.shopflowers.controller.application;

import com.example.shopflowers.model.dao.DAOFactory;
import com.example.shopflowers.model.dao.FlowerProductDAO;
import com.example.shopflowers.model.entity.FlowerProduct;

import java.sql.SQLException;
import java.util.List;

public class BrowseCatalogController {

    private final FlowerProductDAO flowerProductDAO;

    public BrowseCatalogController() {
        try {
            this.flowerProductDAO = DAOFactory.getFlowerProductDAO();
        } catch (SQLException e) {
            throw new IllegalStateException("Impossibile inizializzare la DAO dei prodotti.", e);
        }
    }

    public List<FlowerProduct> getAllProducts() throws SQLException {
        return flowerProductDAO.findAll();
    }
}