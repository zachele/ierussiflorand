package com.example.shopflowers.model.dao;

import com.example.shopflowers.model.entity.CustomBouquetOrderData;
import com.example.shopflowers.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CustomBouquetOrderDAO {

    public void save(CustomBouquetOrderData bouquetData) throws SQLException {
        String query = "INSERT INTO custom_bouquet_order (order_id, size, packaging, card_included, vase_included, total_price) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, bouquetData.getOrderId());
            preparedStatement.setString(2, bouquetData.getSize());
            preparedStatement.setString(3, bouquetData.getPackaging());
            preparedStatement.setBoolean(4, bouquetData.isCardIncluded());
            preparedStatement.setBoolean(5, bouquetData.isVaseIncluded());
            preparedStatement.setDouble(6, bouquetData.getTotalPrice());

            preparedStatement.executeUpdate();
        }
    }
}