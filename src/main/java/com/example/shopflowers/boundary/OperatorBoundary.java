package com.example.shopflowers.boundary;

import com.example.shopflowers.controller.application.ManageOperatorController;
import com.example.shopflowers.exception.InvalidOperatorDataException;
import com.example.shopflowers.exception.UserAlreadyExistsException;
import com.example.shopflowers.model.bean.OperatorBean;

import java.sql.SQLException;

public class OperatorBoundary {

    private final ManageOperatorController manageOperatorController;

    public OperatorBoundary() {
        this.manageOperatorController = new ManageOperatorController();
    }

    public boolean createOperator(
            String name,
            String surname,
            String username,
            String password,
            String salary,
            String contractYear,
            String annualHours
    ) throws SQLException, UserAlreadyExistsException, InvalidOperatorDataException {

        OperatorBean operatorBean = new OperatorBean();
        operatorBean.setName(name);
        operatorBean.setSurname(surname);
        operatorBean.setUsername(username);
        operatorBean.setPassword(password);
        operatorBean.setSalary(salary);
        operatorBean.setContractYear(contractYear);
        operatorBean.setAnnualHours(annualHours);

        return manageOperatorController.createOperator(operatorBean);
    }

    public boolean updateOperator(
            int userId,
            String name,
            String surname,
            String salary,
            String contractYear,
            String annualHours
    ) throws SQLException, InvalidOperatorDataException {

        OperatorBean operatorBean = new OperatorBean();
        operatorBean.setUserId(userId);
        operatorBean.setName(name);
        operatorBean.setSurname(surname);
        operatorBean.setSalary(salary);
        operatorBean.setContractYear(contractYear);
        operatorBean.setAnnualHours(annualHours);

        return manageOperatorController.updateOperator(operatorBean);
    }

    public void deleteOperator(int userId) throws SQLException {
        manageOperatorController.deleteOperator(userId);
    }
}