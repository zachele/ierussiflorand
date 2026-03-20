package com.example.shopflowers.controller.application;

import com.example.shopflowers.model.dao.UserDAO;
import com.example.shopflowers.model.entity.User;

import java.sql.SQLException;

public class LoginController {

    private final UserDAO userDAO;

    public LoginController() {
        this.userDAO = new UserDAO();
    }

    public User login(String username, String password) throws SQLException {
        return userDAO.findByUsernameAndPassword(username, password);
    }
}
