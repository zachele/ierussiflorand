package com.example.shopflowers.controller.application;

import com.example.shopflowers.model.bean.OperatorBean;
import com.example.shopflowers.model.dao.DAOFactory;
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
        try {
            this.userDAO = DAOFactory.getUserDAO();
            this.operatorDetailsDAO = DAOFactory.getOperatorDetailsDAO();
        } catch (SQLException e) {
            throw new IllegalStateException("Impossibile inizializzare le DAO degli operatori.", e);
        }
    }

    public boolean createOperator(OperatorBean operatorBean) throws SQLException {
        if (!isValidCreateBean(operatorBean)) {
            return false;
        }

        if (userDAO.existsByUsername(operatorBean.getUsername())) {
            return false;
        }

        OperatorDetails operatorDetails = parseOperatorDetails(-1, operatorBean);
        if (operatorDetails == null) {
            return false;
        }

        User operator = new User(
                operatorBean.getName(),
                operatorBean.getSurname(),
                operatorBean.getUsername(),
                operatorBean.getPassword(),
                "OPERATOR"
        );

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

    public boolean updateOperator(OperatorBean operatorBean) throws SQLException {
        if (!isValidUpdateBean(operatorBean)) {
            return false;
        }

        OperatorDetails operatorDetails = parseOperatorDetails(operatorBean.getUserId(), operatorBean);
        if (operatorDetails == null) {
            return false;
        }

        userDAO.updateNameAndSurname(
                operatorBean.getUserId(),
                operatorBean.getName(),
                operatorBean.getSurname()
        );
        operatorDetailsDAO.update(operatorDetails);

        return true;
    }

    public void deleteOperator(int userId) throws SQLException {
        operatorDetailsDAO.deleteByUserId(userId);
        userDAO.deleteById(userId);
    }

    private boolean isValidCreateBean(OperatorBean operatorBean) {
        return operatorBean != null
                && isNotBlank(operatorBean.getName())
                && isNotBlank(operatorBean.getSurname())
                && isNotBlank(operatorBean.getUsername())
                && isNotBlank(operatorBean.getPassword())
                && isNotBlank(operatorBean.getSalary())
                && isNotBlank(operatorBean.getContractYear())
                && isNotBlank(operatorBean.getAnnualHours());
    }

    private boolean isValidUpdateBean(OperatorBean operatorBean) {
        return operatorBean != null
                && operatorBean.getUserId() > 0
                && isNotBlank(operatorBean.getName())
                && isNotBlank(operatorBean.getSurname())
                && isNotBlank(operatorBean.getSalary())
                && isNotBlank(operatorBean.getContractYear())
                && isNotBlank(operatorBean.getAnnualHours());
    }

    private OperatorDetails parseOperatorDetails(int userId, OperatorBean operatorBean) {
        final double salary;
        final int contractYear;
        final int annualHours;

        try {
            salary = Double.parseDouble(operatorBean.getSalary());
            contractYear = Integer.parseInt(operatorBean.getContractYear());
            annualHours = Integer.parseInt(operatorBean.getAnnualHours());
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