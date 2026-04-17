package com.example.shopflowers.controller.application;

import com.example.shopflowers.exception.InvalidPasswordChangeException;
import com.example.shopflowers.model.bean.ChangePasswordBean;
import com.example.shopflowers.model.dao.DAOFactory;
import com.example.shopflowers.model.dao.UserDAO;

import java.sql.SQLException;

public class ChangePasswordController {

    private final UserDAO userDAO;

    public ChangePasswordController() {
            this.userDAO = DAOFactory.getUserDAO();
    }

    public boolean changePassword(ChangePasswordBean changePasswordBean)
            throws SQLException, InvalidPasswordChangeException {

        if (!isValidChangePasswordBean(changePasswordBean)) {
            return false;
        }

        if (!changePasswordBean.getNewPassword().equals(changePasswordBean.getConfirmPassword())) {
            throw new InvalidPasswordChangeException("La nuova password e la conferma non coincidono.");
        }

        if (!userDAO.existsByUsernameAndPassword(
                changePasswordBean.getUsername(),
                changePasswordBean.getOldPassword()
        )) {
            throw new InvalidPasswordChangeException("La password attuale non è corretta.");
        }

        userDAO.updatePassword(
                changePasswordBean.getUsername(),
                changePasswordBean.getNewPassword()
        );
        return true;
    }

    private boolean isValidChangePasswordBean(ChangePasswordBean changePasswordBean) {
        return changePasswordBean != null
                && isNotBlank(changePasswordBean.getUsername())
                && isNotBlank(changePasswordBean.getOldPassword())
                && isNotBlank(changePasswordBean.getNewPassword())
                && isNotBlank(changePasswordBean.getConfirmPassword());
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }
}