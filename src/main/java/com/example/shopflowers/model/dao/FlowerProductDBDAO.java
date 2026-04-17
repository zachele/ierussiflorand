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

    private static final String INSERT_PRODUCT = """
            INSERT INTO flower_product
            (name, price, color, variety, stock_quantity, image_name)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

    private static final String SELECT_ALL_PRODUCTS = """
            SELECT id, name, price, color, variety, stock_quantity, image_name
            FROM flower_product
            """;

    private static final String UPDATE_PRODUCT = """
            UPDATE flower_product
            SET name = ?, price = ?, color = ?, variety = ?, stock_quantity = ?, image_name = ?
            WHERE id = ?
            """;

    private static final String DELETE_PRODUCT_BY_ID = """
            DELETE FROM flower_product
            WHERE id = ?
            """;

    private static final String SELECT_PRODUCT_BY_ID = """
            SELECT id, name, price, color, variety, stock_quantity, image_name
            FROM flower_product
            WHERE id = ?
            """;

    private static final String UPDATE_PRODUCT_STOCK = """
            UPDATE flower_product
            SET stock_quantity = ?
            WHERE id = ?
            """;

    @Override
    public void save(FlowerProduct product) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_PRODUCT)) {

            bindProductFields(preparedStatement, product);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public List<FlowerProduct> findAll() throws SQLException {
        List<FlowerProduct> products = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_PRODUCTS);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                products.add(mapFlowerProduct(resultSet));
            }
        }

        return products;
    }

    @Override
    public void update(FlowerProduct product) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_PRODUCT)) {

            bindProductFields(preparedStatement, product);
            preparedStatement.setInt(7, product.getId());
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void deleteById(int id) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_PRODUCT_BY_ID)) {

            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public FlowerProduct findById(int id) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_PRODUCT_BY_ID)) {

            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }

                return mapFlowerProduct(resultSet);
            }
        }
    }

    @Override
    public void updateStock(int productId, int newStock) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_PRODUCT_STOCK)) {

            preparedStatement.setInt(1, newStock);
            preparedStatement.setInt(2, productId);
            preparedStatement.executeUpdate();
        }
    }

    private void bindProductFields(PreparedStatement preparedStatement, FlowerProduct product) throws SQLException {
        preparedStatement.setString(1, product.getName());
        preparedStatement.setDouble(2, product.getPrice());
        preparedStatement.setString(3, product.getColor());
        preparedStatement.setString(4, product.getVariety());
        preparedStatement.setInt(5, product.getStockQuantity());
        preparedStatement.setString(6, product.getImageName());
    }

    private FlowerProduct mapFlowerProduct(ResultSet resultSet) throws SQLException {
        return new FlowerProduct(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getDouble("price"),
                resultSet.getString("color"),
                resultSet.getString("variety"),
                resultSet.getInt("stock_quantity"),
                resultSet.getString("image_name")
        );
    }
}