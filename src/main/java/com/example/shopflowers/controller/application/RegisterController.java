package com.example.shopflowers.controller.application;

import com.example.shopflowers.exception.UserAlreadyExistsException;
import com.example.shopflowers.model.bean.RegisterUserBean;
import com.example.shopflowers.model.dao.DAOFactory;
import com.example.shopflowers.model.dao.UserDAO;
import com.example.shopflowers.model.entity.User;

import java.sql.SQLException;

public class RegisterController {

    private final UserDAO userDAO;

    public RegisterController() {
            this.userDAO = DAOFactory.getUserDAO();
    }

    public boolean registerCustomer(RegisterUserBean registerUserBean)
            throws SQLException, UserAlreadyExistsException {

        if (!isValidRegisterBean(registerUserBean)) {
            return false;
        }

        if (userDAO.existsByUsername(registerUserBean.getUsername())) {
            throw new UserAlreadyExistsException("Username già esistente.");
        }

        User user = new User(
                registerUserBean.getName(),
                registerUserBean.getSurname(),
                registerUserBean.getUsername(),
                registerUserBean.getPassword(),
                "CUSTOMER"
        );

        userDAO.save(user);
        return true;
    }

    private boolean isValidRegisterBean(RegisterUserBean registerUserBean) {
        return registerUserBean != null
                && isNotBlank(registerUserBean.getName())
                && isNotBlank(registerUserBean.getSurname())
                && isNotBlank(registerUserBean.getUsername())
                && isNotBlank(registerUserBean.getPassword());
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }
}