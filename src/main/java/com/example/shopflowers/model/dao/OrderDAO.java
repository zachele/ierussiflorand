package com.example.shopflowers.model.dao;

import com.example.shopflowers.model.entity.CartItem;
import com.example.shopflowers.model.entity.Order;
import com.example.shopflowers.model.entity.OrderItemSummary;
import com.example.shopflowers.model.entity.OrderSummary;
import com.example.shopflowers.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    public int saveOrder(Order order) throws SQLException {
        String query = "INSERT INTO orders (username, delivery_mode, delivery_address, payment_method, status, total) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, order.getUsername());
            preparedStatement.setString(2, order.getDeliveryMode());
            preparedStatement.setString(3, order.getDeliveryAddress());
            preparedStatement.setString(4, order.getPaymentMethod());
            preparedStatement.setString(5, order.getStatus());
            preparedStatement.setDouble(6, order.getTotal());

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

    public List<OrderSummary> findAllOrders() throws SQLException {
        String query = "SELECT id, username, delivery_mode, delivery_address, payment_method, status, total, order_date FROM orders ORDER BY order_date DESC";
        List<OrderSummary> orders = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                OrderSummary order = new OrderSummary(
                        resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getString("delivery_mode"),
                        resultSet.getString("delivery_address"),
                        resultSet.getString("payment_method"),
                        resultSet.getString("status"),
                        resultSet.getDouble("total"),
                        resultSet.getTimestamp("order_date").toString()
                );
                orders.add(order);
            }
        }

        return orders;
    }

    public List<OrderSummary> findOrdersByUsername(String username) throws SQLException {
        String query = "SELECT id, username, delivery_mode, delivery_address, payment_method, status, total, order_date FROM orders WHERE username = ? ORDER BY order_date DESC";
        List<OrderSummary> orders = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    OrderSummary order = new OrderSummary(
                            resultSet.getInt("id"),
                            resultSet.getString("username"),
                            resultSet.getString("delivery_mode"),
                            resultSet.getString("delivery_address"),
                            resultSet.getString("payment_method"),
                            resultSet.getString("status"),
                            resultSet.getDouble("total"),
                            resultSet.getTimestamp("order_date").toString()
                    );
                    orders.add(order);
                }
            }
        }

        return orders;
    }

    public List<OrderItemSummary> findItemsByOrderId(int orderId) throws SQLException {
        String query = """
                SELECT fp.name AS product_name, oi.quantity, oi.unit_price
                FROM order_item oi
                JOIN flower_product fp ON oi.product_id = fp.id
                WHERE oi.order_id = ?
                """;

        List<OrderItemSummary> items = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, orderId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    OrderItemSummary item = new OrderItemSummary(
                            resultSet.getString("product_name"),
                            resultSet.getInt("quantity"),
                            resultSet.getDouble("unit_price")
                    );
                    items.add(item);
                }
            }
        }

        return items;
    }

    public void updateOrderStatus(int orderId, String newStatus) throws SQLException {
        String query = "UPDATE orders SET status = ? WHERE id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, newStatus);
            preparedStatement.setInt(2, orderId);
            preparedStatement.executeUpdate();
        }
    }
    public List<OrderSummary> findActiveOrders() throws SQLException {
        String query = "SELECT id, username, delivery_mode, delivery_address, payment_method, status, total, order_date " +
                "FROM orders WHERE status <> 'CONSEGNATO' ORDER BY order_date DESC";

        List<OrderSummary> orders = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                OrderSummary order = new OrderSummary(
                        resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getString("delivery_mode"),
                        resultSet.getString("delivery_address"),
                        resultSet.getString("payment_method"),
                        resultSet.getString("status"),
                        resultSet.getDouble("total"),
                        resultSet.getTimestamp("order_date").toString()
                );
                orders.add(order);
            }
        }

        return orders;
    }

    public List<OrderSummary> findCompletedOrders() throws SQLException {
        String query = "SELECT id, username, delivery_mode, delivery_address, payment_method, status, total, order_date " +
                "FROM orders WHERE status = 'CONSEGNATO' ORDER BY order_date DESC";

        List<OrderSummary> orders = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                OrderSummary order = new OrderSummary(
                        resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getString("delivery_mode"),
                        resultSet.getString("delivery_address"),
                        resultSet.getString("payment_method"),
                        resultSet.getString("status"),
                        resultSet.getDouble("total"),
                        resultSet.getTimestamp("order_date").toString()
                );
                orders.add(order);
            }
        }

        return orders;
    }
}