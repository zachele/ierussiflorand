package com.example.shopflowers.controller.application;

import com.example.shopflowers.model.dao.UserDAO;
import com.example.shopflowers.model.entity.User;

import java.sql.SQLException;

public class ManageOperatorController {

    private final UserDAO userDAO;

    public ManageOperatorController() {
        this.userDAO = new UserDAO();
    }

    public boolean createOperator(String name, String surname, String username, String password) throws SQLException {
        if (name == null || name.isBlank()
                || surname == null || surname.isBlank()
                || username == null || username.isBlank()
                || password == null || password.isBlank()) {
            return false;
        }

        if (userDAO.existsByUsername(username)) {
            return false;
        }

        User operator = new User(name, surname, username, password, "OPERATOR");
        userDAO.save(operator);
        return true;
    }
}