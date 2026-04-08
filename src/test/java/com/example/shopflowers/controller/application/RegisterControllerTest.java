package com.example.shopflowers.controller.application;

import com.example.shopflowers.config.AppConfig;
import com.example.shopflowers.config.AppMode;
import com.example.shopflowers.exception.UserAlreadyExistsException;
import com.example.shopflowers.model.bean.RegisterUserBean;
import com.example.shopflowers.model.dao.DAOFactory;
import com.example.shopflowers.model.dao.UserDAO;
import com.example.shopflowers.model.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class RegisterControllerTest {

    private RegisterController registerController;
    private UserDAO userDAO;

    @BeforeEach
    void setUp() throws SQLException {
        AppConfig.setMode(AppMode.DEMO);
        registerController = new RegisterController();
        userDAO = DAOFactory.getUserDAO();
    }

    @Test
    void registerCustomer_validData_shouldRegisterSuccessfully() throws SQLException, UserAlreadyExistsException {
        RegisterUserBean registerUserBean = new RegisterUserBean();
        registerUserBean.setName("Giulia");
        registerUserBean.setSurname("Verdi");
        registerUserBean.setUsername("giulia_verdi_test");
        registerUserBean.setPassword("password123");

        boolean result = registerController.registerCustomer(registerUserBean);

        assertTrue(result);

        User savedUser = userDAO.findByUsernameAndPassword("giulia_verdi_test", "password123");
        assertNotNull(savedUser);
        assertEquals("CUSTOMER", savedUser.getRole());
    }

    @Test
    void registerCustomer_existingUsername_shouldThrowUserAlreadyExistsException() {
        RegisterUserBean registerUserBean = new RegisterUserBean();
        registerUserBean.setName("Mario");
        registerUserBean.setSurname("Rossi");
        registerUserBean.setUsername("mario_rossi");
        registerUserBean.setPassword("cliente123");

        assertThrows(
                UserAlreadyExistsException.class,
                () -> registerController.registerCustomer(registerUserBean)
        );
    }

    @Test
    void registerCustomer_blankName_shouldReturnFalse() throws SQLException, UserAlreadyExistsException {
        RegisterUserBean registerUserBean = new RegisterUserBean();
        registerUserBean.setName("");
        registerUserBean.setSurname("Verdi");
        registerUserBean.setUsername("utente_test_1");
        registerUserBean.setPassword("password123");

        boolean result = registerController.registerCustomer(registerUserBean);

        assertFalse(result);
    }

    @Test
    void registerCustomer_blankSurname_shouldReturnFalse() throws SQLException, UserAlreadyExistsException {
        RegisterUserBean registerUserBean = new RegisterUserBean();
        registerUserBean.setName("Giulia");
        registerUserBean.setSurname("");
        registerUserBean.setUsername("utente_test_2");
        registerUserBean.setPassword("password123");

        boolean result = registerController.registerCustomer(registerUserBean);

        assertFalse(result);
    }

    @Test
    void registerCustomer_blankUsername_shouldReturnFalse() throws SQLException, UserAlreadyExistsException {
        RegisterUserBean registerUserBean = new RegisterUserBean();
        registerUserBean.setName("Giulia");
        registerUserBean.setSurname("Verdi");
        registerUserBean.setUsername("");
        registerUserBean.setPassword("password123");

        boolean result = registerController.registerCustomer(registerUserBean);

        assertFalse(result);
    }

    @Test
    void registerCustomer_blankPassword_shouldReturnFalse() throws SQLException, UserAlreadyExistsException {
        RegisterUserBean registerUserBean = new RegisterUserBean();
        registerUserBean.setName("Giulia");
        registerUserBean.setSurname("Verdi");
        registerUserBean.setUsername("utente_test_3");
        registerUserBean.setPassword("");

        boolean result = registerController.registerCustomer(registerUserBean);

        assertFalse(result);
    }

    @Test
    void registerCustomer_nullBean_shouldReturnFalse() throws SQLException, UserAlreadyExistsException {
        boolean result = registerController.registerCustomer(null);

        assertFalse(result);
    }
}