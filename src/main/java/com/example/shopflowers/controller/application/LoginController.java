package com.example.shopflowers.controller.application;

import com.example.shopflowers.exception.InvalidCredentialsException;
import com.example.shopflowers.model.bean.LoginBean;
import com.example.shopflowers.model.dao.DAOFactory;
import com.example.shopflowers.model.dao.UserDAO;
import com.example.shopflowers.model.entity.User;

import java.sql.SQLException;

public class LoginController {

    private final UserDAO userDAO;

    public LoginController() {
        this.userDAO = DAOFactory.getUserDAO();
    }

    public User login(LoginBean loginBean) throws SQLException, InvalidCredentialsException {
        User user = userDAO.findByUsernameAndPassword(
                loginBean.getUsername(),
                loginBean.getPassword()
        );

        if (user == null) {
            throw new InvalidCredentialsException("Login non riuscito. Controlla username e password.");
        }

        return user;
    }
}