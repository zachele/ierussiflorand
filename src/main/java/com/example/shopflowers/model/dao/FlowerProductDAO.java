package com.example.shopflowers.model.dao;

import com.example.shopflowers.model.entity.FlowerProduct;

import java.sql.SQLException;
import java.util.List;

public interface FlowerProductDAO {

    void save(FlowerProduct product) throws SQLException;

    List<FlowerProduct> findAll() throws SQLException;

    void update(FlowerProduct product) throws SQLException;

    void deleteById(int id) throws SQLException;

    FlowerProduct findById(int id) throws SQLException;

    void updateStock(int productId, int newStock) throws SQLException;
}