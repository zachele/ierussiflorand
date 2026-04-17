package com.example.shopflowers.model.dao;

import com.example.shopflowers.model.entity.OperatorDetails;
import com.example.shopflowers.model.entity.OperatorFullData;
import com.example.shopflowers.model.entity.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OperatorDetailsMemoryDAO implements OperatorDetailsDAO {

    private static final List<OperatorDetails> OPERATOR_DETAILS_LIST = new ArrayList<>();
    private static boolean initialized = false;

    private final UserDAO userDAO;

    public OperatorDetailsMemoryDAO() {
        this.userDAO = new UserMemoryDAO();
        initializeIfNeeded();
    }

    @Override
    public void save(OperatorDetails operatorDetails) {
        OPERATOR_DETAILS_LIST.add(copyOperatorDetails(operatorDetails));
    }

    @Override
    public void deleteByUserId(int userId) {
        OPERATOR_DETAILS_LIST.removeIf(details -> details.getUserId() == userId);
    }

    @Override
    public void update(OperatorDetails operatorDetails) {
        for (int i = 0; i < OPERATOR_DETAILS_LIST.size(); i++) {
            if (OPERATOR_DETAILS_LIST.get(i).getUserId() == operatorDetails.getUserId()) {
                OPERATOR_DETAILS_LIST.set(i, copyOperatorDetails(operatorDetails));
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

    private static void initializeIfNeeded() {
        if (initialized) {
            return;
        }

        OPERATOR_DETAILS_LIST.clear();
        OPERATOR_DETAILS_LIST.add(new OperatorDetails(3, 1450.0, 2022, 1600));
        initialized = true;
    }

    private OperatorDetails findByUserId(int userId) {
        for (OperatorDetails details : OPERATOR_DETAILS_LIST) {
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