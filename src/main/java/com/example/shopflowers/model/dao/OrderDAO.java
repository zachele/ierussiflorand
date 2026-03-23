package com.example.shopflowers.model.dao;

import com.example.shopflowers.model.entity.CartItem;
import com.example.shopflowers.model.entity.Order;
import com.example.shopflowers.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderDAO {

    public int saveOrder(Order order) throws SQLException {
        String query = "INSERT INTO orders (username, delivery_mode, payment_method, total) VALUES (?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, order.getUsername());
            preparedStatement.setString(2, order.getDeliveryMode());
            preparedStatement.setString(3, order.getPaymentMethod());
            preparedStatement.setDouble(4, order.getTotal());

            preparedStatement.executeUpdate();

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }

        throw new SQLException("Impossibile ottenere l'ID dell'ordine creato.");
    }

    public void saveOrderItems(int orderId, Order order) throws SQLException {
        String query = "INSERT INTO order_item (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            for (CartItem item : order.getItems()) {
                preparedStatement.setInt(1, orderId);
                preparedStatement.setInt(2, item.getProduct().getId());
                preparedStatement.setInt(3, item.getQuantity());
                preparedStatement.setDouble(4, item.getProduct().getPrice());
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
        }
    }
}