package com.example.shopflowers.controller.application;

import com.example.shopflowers.model.dao.OperatorDetailsDAO;
import com.example.shopflowers.model.dao.UserDAO;
import com.example.shopflowers.model.entity.OperatorDetails;
import com.example.shopflowers.model.entity.OperatorFullData;
import com.example.shopflowers.model.entity.User;

import java.sql.SQLException;
import java.util.List;

public class ManageOperatorController {

    private final UserDAO userDAO;
    private final OperatorDetailsDAO operatorDetailsDAO;

    public ManageOperatorController() {
        this.userDAO = new UserDAO();
        this.operatorDetailsDAO = new OperatorDetailsDAO();
    }

    public boolean createOperator(String name, String surname, String username, String password,
                                  String salaryText, String contractYearText, String annualHoursText) throws SQLException {

        if (name == null || name.isBlank()
                || surname == null || surname.isBlank()
                || username == null || username.isBlank()
                || password == null || password.isBlank()
                || salaryText == null || salaryText.isBlank()
                || contractYearText == null || contractYearText.isBlank()
                || annualHoursText == null || annualHoursText.isBlank()) {
            return false;
        }

        if (userDAO.existsByUsername(username)) {
            return false;
        }

        double salary;
        int contractYear;
        int annualHours;

        try {
            salary = Double.parseDouble(salaryText);
            contractYear = Integer.parseInt(contractYearText);
            annualHours = Integer.parseInt(annualHoursText);
        } catch (NumberFormatException e) {
            return false;
        }

        if (salary < 0 || annualHours < 0 || contractYear < 2000) {
            return false;
        }

        User operator = new User(name, surname, username, password, "OPERATOR");
        int userId = userDAO.saveAndReturnId(operator);

        OperatorDetails operatorDetails = new OperatorDetails(userId, salary, contractYear, annualHours);
        operatorDetailsDAO.save(operatorDetails);

        return true;
    }

    public List<OperatorFullData> getAllOperators() throws SQLException {
        return operatorDetailsDAO.findAllOperatorFullData();
    }

    public boolean updateOperator(int userId, String name, String surname,
                                  String salaryText, String contractYearText, String annualHoursText) throws SQLException {

        if (name == null || name.isBlank()
                || surname == null || surname.isBlank()
                || salaryText == null || salaryText.isBlank()
                || contractYearText == null || contractYearText.isBlank()
                || annualHoursText == null || annualHoursText.isBlank()) {
            return false;
        }

        double salary;
        int contractYear;
        int annualHours;

        try {
            salary = Double.parseDouble(salaryText);
            contractYear = Integer.parseInt(contractYearText);
            annualHours = Integer.parseInt(annualHoursText);
        } catch (NumberFormatException e) {
            return false;
        }

        if (salary < 0 || annualHours < 0 || contractYear < 2000) {
            return false;
        }

        userDAO.updateNameAndSurname(userId, name, surname);

        OperatorDetails operatorDetails = new OperatorDetails(userId, salary, contractYear, annualHours);
        operatorDetailsDAO.update(operatorDetails);

        return true;
    }

    public boolean deleteOperator(int userId) throws SQLException {
        operatorDetailsDAO.deleteByUserId(userId);
        userDAO.deleteById(userId);
        return true;
    }
}