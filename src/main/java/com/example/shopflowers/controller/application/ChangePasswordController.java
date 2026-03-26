package com.example.shopflowers.controller.application;

import com.example.shopflowers.model.dao.UserDAO;

import java.sql.SQLException;

public class ChangePasswordController {

    private final UserDAO userDAO;

    public ChangePasswordController() {
        this.userDAO = new UserDAO();
    }

    public boolean changePassword(String username, String oldPassword, String newPassword, String confirmPassword)
            throws SQLException {

        if (username == null || username.isBlank()
                || oldPassword == null || oldPassword.isBlank()
                || newPassword == null || newPassword.isBlank()
                || confirmPassword == null || confirmPassword.isBlank()) {
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            return false;
        }

        if (!userDAO.existsByUsernameAndPassword(username, oldPassword)) {
            return false;
        }

        userDAO.updatePassword(username, newPassword);
        return true;
    }
}