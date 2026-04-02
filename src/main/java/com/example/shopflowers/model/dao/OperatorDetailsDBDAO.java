package com.example.shopflowers.model.dao;

import com.example.shopflowers.model.entity.OperatorDetails;
import com.example.shopflowers.model.entity.OperatorFullData;
import com.example.shopflowers.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OperatorDetailsDBDAO implements OperatorDetailsDAO {

    @Override
    public void save(OperatorDetails operatorDetails) throws SQLException {
        String query = "INSERT INTO operator_details (user_id, salary, contract_year, annual_hours) VALUES (?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, operatorDetails.getUserId());
            preparedStatement.setDouble(2, operatorDetails.getSalary());
            preparedStatement.setInt(3, operatorDetails.getContractYear());
            preparedStatement.setInt(4, operatorDetails.getAnnualHours());

            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void deleteByUserId(int userId) throws SQLException {
        String query = "DELETE FROM operator_details WHERE user_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void update(OperatorDetails operatorDetails) throws SQLException {
        String query = "UPDATE operator_details SET salary = ?, contract_year = ?, annual_hours = ? WHERE user_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setDouble(1, operatorDetails.getSalary());
            preparedStatement.setInt(2, operatorDetails.getContractYear());
            preparedStatement.setInt(3, operatorDetails.getAnnualHours());
            preparedStatement.setInt(4, operatorDetails.getUserId());

            preparedStatement.executeUpdate();
        }
    }

    @Override
    public List<OperatorFullData> findAllOperatorFullData() throws SQLException {
        String query = """
                SELECT u.id, u.name, u.surname, u.username, od.salary, od.contract_year, od.annual_hours
                FROM users u
                JOIN operator_details od ON u.id = od.user_id
                WHERE u.role = 'OPERATOR'
                ORDER BY u.surname, u.name
                """;

        List<OperatorFullData> operators = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                operators.add(new OperatorFullData(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("surname"),
                        resultSet.getString("username"),
                        resultSet.getDouble("salary"),
                        resultSet.getInt("contract_year"),
                        resultSet.getInt("annual_hours")
                ));
            }
        }

        return operators;
    }
}