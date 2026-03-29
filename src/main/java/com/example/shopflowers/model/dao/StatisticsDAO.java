package com.example.shopflowers.model.dao;

import com.example.shopflowers.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StatisticsDAO {

    public int getTotalOrders() throws SQLException {
        String query = "SELECT COUNT(*) FROM orders";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public double getTotalRevenue() throws SQLException {
        String query = "SELECT SUM(total) FROM orders WHERE status = 'CONSEGNATO'";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0;
    }

    public int getDeliveredOrders() throws SQLException {
        String query = "SELECT COUNT(*) FROM orders WHERE status = 'CONSEGNATO'";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public int getActiveOrders() throws SQLException {
        String query = "SELECT COUNT(*) FROM orders WHERE status <> 'CONSEGNATO'";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public int getTotalClients() throws SQLException {
        String query = "SELECT COUNT(*) FROM users WHERE role = 'CUSTOMER'";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public int getTotalOperators() throws SQLException {
        String query = "SELECT COUNT(*) FROM users WHERE role = 'OPERATOR'";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public String getMostSoldProduct() throws SQLException {
        String query = """
                SELECT fp.name, SUM(oi.quantity) AS total_sold
                FROM order_item oi
                JOIN flower_product fp ON oi.product_id = fp.id
                GROUP BY fp.name
                ORDER BY total_sold DESC
                LIMIT 1
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getString("name");
            }
        }
        return "Nessuno";
    }
    public java.util.Map<String, Integer> getSoldProductsDistribution() throws SQLException {
        String query = """
            SELECT fp.name, SUM(oi.quantity) AS total_sold
            FROM order_item oi
            JOIN flower_product fp ON oi.product_id = fp.id
            GROUP BY fp.name
            HAVING SUM(oi.quantity) > 0
            ORDER BY total_sold DESC
            """;

        java.util.Map<String, Integer> distribution = new java.util.LinkedHashMap<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                distribution.put(
                        rs.getString("name"),
                        rs.getInt("total_sold")
                );
            }
        }

        return distribution;
    }
}
