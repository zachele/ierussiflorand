package com.example.shopflowers.boundary;

import com.example.shopflowers.controller.application.RegisterController;
import com.example.shopflowers.exception.UserAlreadyExistsException;
import com.example.shopflowers.model.bean.RegisterUserBean;

import java.sql.SQLException;

public class RegisterBoundary {

    private final RegisterController registerController;

    public RegisterBoundary() {
        this.registerController = new RegisterController();
    }

    public boolean registerCustomer(String name, String surname, String username, String password)
            throws SQLException, UserAlreadyExistsException {

        RegisterUserBean registerUserBean = new RegisterUserBean();
        registerUserBean.setName(name);
        registerUserBean.setSurname(surname);
        registerUserBean.setUsername(username);
        registerUserBean.setPassword(password);

        return registerController.registerCustomer(registerUserBean);
    }
}