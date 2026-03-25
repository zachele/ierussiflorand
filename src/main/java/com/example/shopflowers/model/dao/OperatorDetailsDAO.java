package com.example.shopflowers.model.dao;

import com.example.shopflowers.model.entity.OperatorDetails;
import com.example.shopflowers.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OperatorDetailsDAO {

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
}