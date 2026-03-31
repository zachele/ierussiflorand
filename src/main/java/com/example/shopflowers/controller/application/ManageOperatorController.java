package com.example.shopflowers.controller.application;

import com.example.shopflowers.model.dao.OperatorDetailsDAO;
import com.example.shopflowers.model.dao.UserDAO;
import com.example.shopflowers.model.entity.OperatorDetails;
import com.example.shopflowers.model.entity.OperatorFullData;
import com.example.shopflowers.model.entity.User;

import java.sql.SQLException;
import java.util.List;

public class ManageOperatorController {

    private static final int MIN_CONTRACT_YEAR = 2000;

    private final UserDAO userDAO;
    private final OperatorDetailsDAO operatorDetailsDAO;

    public ManageOperatorController() {
        this.userDAO = new UserDAO();
        this.operatorDetailsDAO = new OperatorDetailsDAO();
    }

    public boolean createOperator(String name, String surname, String username, String password,
                                  String salaryText, String contractYearText, String annualHoursText) throws SQLException {

        if (!areCreateFieldsValid(name, surname, username, password, salaryText, contractYearText, annualHoursText)) {
            return false;
        }

        if (userDAO.existsByUsername(username)) {
            return false;
        }

        OperatorDetails operatorDetails = parseOperatorDetails(-1, salaryText, contractYearText, annualHoursText);
        if (operatorDetails == null) {
            return false;
        }

        User operator = new User(name, surname, username, password, "OPERATOR");
        int userId = userDAO.saveAndReturnId(operator);

        OperatorDetails savedOperatorDetails = new OperatorDetails(
                userId,
                operatorDetails.getSalary(),
                operatorDetails.getContractYear(),
                operatorDetails.getAnnualHours()
        );
        operatorDetailsDAO.save(savedOperatorDetails);

        return true;
    }

    public List<OperatorFullData> getAllOperators() throws SQLException {
        return operatorDetailsDAO.findAllOperatorFullData();
    }

    public boolean updateOperator(int userId, String name, String surname,
                                  String salaryText, String contractYearText, String annualHoursText) throws SQLException {

        if (!areUpdateFieldsValid(name, surname, salaryText, contractYearText, annualHoursText)) {
            return false;
        }

        OperatorDetails operatorDetails = parseOperatorDetails(userId, salaryText, contractYearText, annualHoursText);
        if (operatorDetails == null) {
            return false;
        }

        userDAO.updateNameAndSurname(userId, name, surname);
        operatorDetailsDAO.update(operatorDetails);

        return true;
    }

    public void deleteOperator(int userId) throws SQLException {
        operatorDetailsDAO.deleteByUserId(userId);
        userDAO.deleteById(userId);
    }

    private boolean areCreateFieldsValid(String name, String surname, String username, String password,
                                         String salaryText, String contractYearText, String annualHoursText) {
        return isNotBlank(name)
                && isNotBlank(surname)
                && isNotBlank(username)
                && isNotBlank(password)
                && isNotBlank(salaryText)
                && isNotBlank(contractYearText)
                && isNotBlank(annualHoursText);
    }

    private boolean areUpdateFieldsValid(String name, String surname,
                                         String salaryText, String contractYearText, String annualHoursText) {
        return isNotBlank(name)
                && isNotBlank(surname)
                && isNotBlank(salaryText)
                && isNotBlank(contractYearText)
                && isNotBlank(annualHoursText);
    }

    private OperatorDetails parseOperatorDetails(int userId, String salaryText,
                                                 String contractYearText, String annualHoursText) {
        final double salary;
        final int contractYear;
        final int annualHours;

        try {
            salary = Double.parseDouble(salaryText);
            contractYear = Integer.parseInt(contractYearText);
            annualHours = Integer.parseInt(annualHoursText);
        } catch (NumberFormatException e) {
            return null;
        }

        if (salary < 0 || annualHours < 0 || contractYear < MIN_CONTRACT_YEAR) {
            return null;
        }

        return new OperatorDetails(userId, salary, contractYear, annualHours);
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }
}