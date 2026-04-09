package com.example.shopflowers.boundary;

import com.example.shopflowers.controller.application.LoginController;
import com.example.shopflowers.exception.InvalidCredentialsException;
import com.example.shopflowers.model.bean.LoginBean;
import com.example.shopflowers.model.entity.User;

import java.sql.SQLException;

public class LoginBoundary {

    private final LoginController loginController;

    public LoginBoundary() {
        this.loginController = new LoginController();
    }

    public User login(String username, String password)
            throws SQLException, InvalidCredentialsException {

        LoginBean loginBean = new LoginBean();
        loginBean.setUsername(username);
        loginBean.setPassword(password);

        return loginController.login(loginBean);
    }
}