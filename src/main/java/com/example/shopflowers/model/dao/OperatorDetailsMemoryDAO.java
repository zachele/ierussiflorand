package com.example.shopflowers.model.dao;

import com.example.shopflowers.model.entity.OperatorDetails;
import com.example.shopflowers.model.entity.OperatorFullData;
import com.example.shopflowers.model.entity.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OperatorDetailsMemoryDAO implements OperatorDetailsDAO {

    private static final List<OperatorDetails> operatorDetailsList = new ArrayList<>();
    private static boolean initialized = false;

    private final UserDAO userDAO;

    public OperatorDetailsMemoryDAO() throws SQLException {
        this.userDAO = new UserMemoryDAO();
        initializeIfNeeded();
    }

    @Override
    public void save(OperatorDetails operatorDetails) throws SQLException {
        operatorDetailsList.add(copyOperatorDetails(operatorDetails));
    }

    @Override
    public void deleteByUserId(int userId) throws SQLException {
        operatorDetailsList.removeIf(details -> details.getUserId() == userId);
    }

    @Override
    public void update(OperatorDetails operatorDetails) throws SQLException {
        for (int i = 0; i < operatorDetailsList.size(); i++) {
            if (operatorDetailsList.get(i).getUserId() == operatorDetails.getUserId()) {
                operatorDetailsList.set(i, copyOperatorDetails(operatorDetails));
                return;
            }
        }
    }

    @Override
    public List<OperatorFullData> findAllOperatorFullData() throws SQLException {
        List<OperatorFullData> result = new ArrayList<>();
        List<User> operators = userDAO.findAllOperators();

        for (User user : operators) {
            OperatorDetails details = findByUserId(user.getId());
            if (details != null) {
                result.add(new OperatorFullData(
                        user.getId(),
                        user.getName(),
                        user.getSurname(),
                        user.getUsername(),
                        details.getSalary(),
                        details.getContractYear(),
                        details.getAnnualHours()
                ));
            }
        }

        return result;
    }

    private void initializeIfNeeded() {
        if (initialized) {
            return;
        }

        operatorDetailsList.clear();
        operatorDetailsList.add(new OperatorDetails(3, 1450.0, 2022, 1600));
        initialized = true;
    }

    private OperatorDetails findByUserId(int userId) {
        for (OperatorDetails details : operatorDetailsList) {
            if (details.getUserId() == userId) {
                return details;
            }
        }
        return null;
    }

    private OperatorDetails copyOperatorDetails(OperatorDetails details) {
        return new OperatorDetails(
                details.getUserId(),
                details.getSalary(),
                details.getContractYear(),
                details.getAnnualHours()
        );
    }
}