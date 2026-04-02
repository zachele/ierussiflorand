package com.example.shopflowers.model.dao;

import com.example.shopflowers.model.entity.FlowerProduct;
import com.example.shopflowers.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FlowerProductDBDAO implements FlowerProductDAO {

    @Override
    public void save(FlowerProduct product) throws SQLException {
        String query = "INSERT INTO flower_product (name, price, color, variety, stock_quantity) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, product.getName());
            preparedStatement.setDouble(2, product.getPrice());
            preparedStatement.setString(3, product.getColor());
            preparedStatement.setString(4, product.getVariety());
            preparedStatement.setInt(5, product.getStockQuantity());

            preparedStatement.executeUpdate();
        }
    }

    @Override
    public List<FlowerProduct> findAll() throws SQLException {
        String query = "SELECT id, name, price, color, variety, stock_quantity FROM flower_product";
        List<FlowerProduct> products = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                FlowerProduct product = new FlowerProduct(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getDouble("price"),
                        resultSet.getString("color"),
                        resultSet.getString("variety"),
                        resultSet.getInt("stock_quantity")
                );

                products.add(product);
            }
        }

        return products;
    }

    @Override
    public void update(FlowerProduct product) throws SQLException {
        String query = "UPDATE flower_product SET name = ?, price = ?, color = ?, variety = ?, stock_quantity = ? WHERE id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, product.getName());
            preparedStatement.setDouble(2, product.getPrice());
            preparedStatement.setString(3, product.getColor());
            preparedStatement.setString(4, product.getVariety());
            preparedStatement.setInt(5, product.getStockQuantity());
            preparedStatement.setInt(6, product.getId());

            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void deleteById(int id) throws SQLException {
        String query = "DELETE FROM flower_product WHERE id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public FlowerProduct findById(int id) throws SQLException {
        String query = "SELECT id, name, price, color, variety, stock_quantity FROM flower_product WHERE id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new FlowerProduct(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getDouble("price"),
                            resultSet.getString("color"),
                            resultSet.getString("variety"),
                            resultSet.getInt("stock_quantity")
                    );
                }
            }
        }

        return null;
    }

    @Override
    public void updateStock(int productId, int newStock) throws SQLException {
        String query = "UPDATE flower_product SET stock_quantity = ? WHERE id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, newStock);
            preparedStatement.setInt(2, productId);
            preparedStatement.executeUpdate();
        }
    }
}