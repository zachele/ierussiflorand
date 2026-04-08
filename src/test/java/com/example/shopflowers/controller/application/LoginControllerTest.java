package com.example.shopflowers.controller.application;

import com.example.shopflowers.config.AppConfig;
import com.example.shopflowers.config.AppMode;
import com.example.shopflowers.exception.InvalidCredentialsException;
import com.example.shopflowers.model.bean.LoginBean;
import com.example.shopflowers.model.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class LoginControllerTest {

    private LoginController loginController;

    @BeforeEach
    void setUp() {
        AppConfig.setMode(AppMode.DEMO);
        loginController = new LoginController();
    }

    @Test
    void login_validAdminCredentials_shouldReturnAdminUser() throws SQLException, InvalidCredentialsException {
        LoginBean loginBean = new LoginBean();
        loginBean.setUsername("admin");
        loginBean.setPassword("admin123");

        User user = loginController.login(loginBean);

        assertNotNull(user);
        assertEquals("admin", user.getUsername());
        assertEquals("ADMIN", user.getRole());
    }

    @Test
    void login_validCustomerCredentials_shouldReturnCustomerUser() throws SQLException, InvalidCredentialsException {
        LoginBean loginBean = new LoginBean();
        loginBean.setUsername("mario_rossi");
        loginBean.setPassword("cliente123");

        User user = loginController.login(loginBean);

        assertNotNull(user);
        assertEquals("mario_rossi", user.getUsername());
        assertEquals("CUSTOMER", user.getRole());
    }

    @Test
    void login_validOperatorCredentials_shouldReturnOperatorUser() throws SQLException, InvalidCredentialsException {
        LoginBean loginBean = new LoginBean();
        loginBean.setUsername("operatore");
        loginBean.setPassword("operatore123");

        User user = loginController.login(loginBean);

        assertNotNull(user);
        assertEquals("operatore", user.getUsername());
        assertEquals("OPERATOR", user.getRole());
    }

    @Test
    void login_wrongPassword_shouldThrowInvalidCredentialsException() {
        LoginBean loginBean = new LoginBean();
        loginBean.setUsername("admin");
        loginBean.setPassword("password_sbagliata");

        assertThrows(
                InvalidCredentialsException.class,
                () -> loginController.login(loginBean)
        );
    }

    @Test
    void login_unknownUsername_shouldThrowInvalidCredentialsException() {
        LoginBean loginBean = new LoginBean();
        loginBean.setUsername("utente_inesistente");
        loginBean.setPassword("admin123");

        assertThrows(
                InvalidCredentialsException.class,
                () -> loginController.login(loginBean)
        );
    }

    @Test
    void login_blankUsername_shouldThrowInvalidCredentialsException() {
        LoginBean loginBean = new LoginBean();
        loginBean.setUsername("");
        loginBean.setPassword("admin123");

        assertThrows(
                InvalidCredentialsException.class,
                () -> loginController.login(loginBean)
        );
    }

    @Test
    void login_blankPassword_shouldThrowInvalidCredentialsException() {
        LoginBean loginBean = new LoginBean();
        loginBean.setUsername("admin");
        loginBean.setPassword("");

        assertThrows(
                InvalidCredentialsException.class,
                () -> loginController.login(loginBean)
        );
    }
}