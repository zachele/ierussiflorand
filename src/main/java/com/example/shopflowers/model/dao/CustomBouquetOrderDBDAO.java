package com.example.shopflowers.model.dao;

import com.example.shopflowers.model.entity.CustomBouquetOrderData;
import com.example.shopflowers.model.entity.CustomBouquetOrderSummary;
import com.example.shopflowers.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomBouquetOrderDBDAO implements CustomBouquetOrderDAO {

    private static final String INSERT_BOUQUET_ORDER = """
            INSERT INTO custom_bouquet_order
            (order_id, size, packaging, card_included, vase_included, total_price)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

    private static final String SELECT_BOUQUET_BY_ORDER_ID = """
            SELECT size, packaging, card_included, vase_included, total_price
            FROM custom_bouquet_order
            WHERE order_id = ?
            """;

    @Override
    public void save(CustomBouquetOrderData bouquetData) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_BOUQUET_ORDER)) {

            preparedStatement.setInt(1, bouquetData.getOrderId());
            preparedStatement.setString(2, bouquetData.getSize());
            preparedStatement.setString(3, bouquetData.getPackaging());
            preparedStatement.setBoolean(4, bouquetData.isCardIncluded());
            preparedStatement.setBoolean(5, bouquetData.isVaseIncluded());
            preparedStatement.setDouble(6, bouquetData.getTotalPrice());

            preparedStatement.executeUpdate();
        }
    }

    @Override
    public CustomBouquetOrderSummary findByOrderId(int orderId) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BOUQUET_BY_ORDER_ID)) {

            preparedStatement.setInt(1, orderId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }

                return new CustomBouquetOrderSummary(
                        resultSet.getString("size"),
                        resultSet.getString("packaging"),
                        resultSet.getBoolean("card_included"),
                        resultSet.getBoolean("vase_included"),
                        resultSet.getDouble("total_price")
                );
            }
        }
    }
}