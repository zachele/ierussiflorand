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
        String query = "INSERT INTO orders (username, delivery_mode, delivery_address, pickup_date, pickup_time, payment_method, status, total) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, order.getUsername());
            preparedStatement.setString(2, order.getDeliveryMode());
            preparedStatement.setString(3, order.getDeliveryAddress());
            preparedStatement.setString(4, order.getPickupDate());
            preparedStatement.setString(5, order.getPickupTime());
            preparedStatement.setString(6, order.getPaymentMethod());
            preparedStatement.setString(7, order.getStatus());
            preparedStatement.setDouble(8, order.getTotal());

            preparedStatement.executeUpdate();

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }

        throw new SQLException("Impossibile ottenere l' ID dell'ordine creato.");
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
        String query = """
            SELECT o.id,
                   o.username,
                   u.name,
                   u.surname,
                   o.delivery_mode,
                   o.delivery_address,
                   o.pickup_date,
                   o.pickup_time,
                   o.payment_method,
                   o.status,
                   o.total,
                   o.order_date
            FROM orders o
            JOIN users u ON o.username = u.username
            ORDER BY o.order_date DESC
            """;
        return findOrdersByQuery(query);
    }

    public List<OrderSummary> findOrdersByUsername(String username) throws SQLException {
        String query = """
            SELECT o.id,
                   o.username,
                   u.name,
                   u.surname,
                   o.delivery_mode,
                   o.delivery_address,
                   o.pickup_date,
                   o.pickup_time,
                   o.payment_method,
                   o.status,
                   o.total,
                   o.order_date
            FROM orders o
            JOIN users u ON o.username = u.username
            WHERE o.username = ?
            ORDER BY o.order_date DESC
            """;
        return findOrdersByQuery(query, username);
    }

    public List<OrderSummary> findActiveOrders() throws SQLException {
        String query = """
            SELECT o.id,
                   o.username,
                   u.name,
                   u.surname,
                   o.delivery_mode,
                   o.delivery_address,
                   o.pickup_date,
                   o.pickup_time,
                   o.payment_method,
                   o.status,
                   o.total,
                   o.order_date
            FROM orders o
            JOIN users u ON o.username = u.username
            WHERE o.status <> 'CONSEGNATO'
            ORDER BY o.order_date DESC
            """;
        return findOrdersByQuery(query);
    }

    public List<OrderSummary> findCompletedOrders() throws SQLException {
        String query = """
            SELECT o.id,
                   o.username,
                   u.name,
                   u.surname,
                   o.delivery_mode,
                   o.delivery_address,
                   o.pickup_date,
                   o.pickup_time,
                   o.payment_method,
                   o.status,
                   o.total,
                   o.order_date
            FROM orders o
            JOIN users u ON o.username = u.username
            WHERE o.status = 'CONSEGNATO'
            ORDER BY o.order_date DESC
            """;
        return findOrdersByQuery(query);
    }

    public List<OrderSummary> findOrdersWithStatusUpdate(String username) throws SQLException {
        String query = """
            SELECT o.id,
                   o.username,
                   u.name,
                   u.surname,
                   o.delivery_mode,
                   o.delivery_address,
                   o.pickup_date,
                   o.pickup_time,
                   o.payment_method,
                   o.status,
                   o.total,
                   o.order_date
            FROM orders o
            JOIN users u ON o.username = u.username
            WHERE o.username = ? AND o.status_notified = FALSE
            ORDER BY o.order_date DESC
            """;
        return findOrdersByQuery(query, username);
    }

    public void markOrdersAsNotified(String username) throws SQLException {
        String query = "UPDATE orders SET status_notified = TRUE WHERE username = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            preparedStatement.executeUpdate();
        }
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
                    items.add(mapOrderItemSummary(resultSet));
                }
            }
        }

        return items;
    }

    public void updateOrderStatus(int orderId, String newStatus) throws SQLException {
        String query = "UPDATE orders SET status = ?, status_notified = FALSE WHERE id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, newStatus);
            preparedStatement.setInt(2, orderId);
            preparedStatement.executeUpdate();
        }
    }

    private List<OrderSummary> findOrdersByQuery(String query, String... params) throws SQLException {
        List<OrderSummary> orders = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            for (int i = 0; i < params.length; i++) {
                preparedStatement.setString(i + 1, params[i]);
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    orders.add(mapOrderSummary(resultSet));
                }
            }
        }

        return orders;
    }

    private OrderSummary mapOrderSummary(ResultSet resultSet) throws SQLException {
        return new OrderSummary(
                resultSet.getInt("id"),
                resultSet.getString("username"),
                resultSet.getString("name"),
                resultSet.getString("surname"),
                resultSet.getString("delivery_mode"),
                resultSet.getString("delivery_address"),
                resultSet.getString("pickup_date"),
                resultSet.getString("pickup_time"),
                resultSet.getString("payment_method"),
                resultSet.getString("status"),
                resultSet.getDouble("total"),
                resultSet.getTimestamp("order_date").toString()
        );
    }

    private OrderItemSummary mapOrderItemSummary(ResultSet resultSet) throws SQLException {
        return new OrderItemSummary(
                resultSet.getString("product_name"),
                resultSet.getInt("quantity"),
                resultSet.getDouble("unit_price")
        );
    }

    public void saveCustomBouquetItems(int orderId, com.example.shopflowers.model.entity.CustomBouquet bouquet) throws SQLException {
        String query = "INSERT INTO order_item (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            for (com.example.shopflowers.model.entity.CustomBouquetItem item : bouquet.getItems()) {
                preparedStatement.setInt(1, orderId);
                preparedStatement.setInt(2, item.getFlowerProduct().getId());
                preparedStatement.setInt(3, item.getQuantity());
                preparedStatement.setDouble(4, item.getFlowerProduct().getPrice());
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
        }
    }
}