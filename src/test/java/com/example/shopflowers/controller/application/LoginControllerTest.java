package com.example.shopflowers.controller.application;

import com.example.shopflowers.config.AppConfig;
import com.example.shopflowers.config.AppMode;
import com.example.shopflowers.exception.InvalidCredentialsException;
import com.example.shopflowers.model.bean.LoginBean;
import com.example.shopflowers.model.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoginControllerTest {

    private LoginController loginController;

    @BeforeEach
    void setUp() {
        AppConfig.setMode(AppMode.DEMO);
        loginController = new LoginController();
    }

    @ParameterizedTest
    @CsvSource({
            "admin,admin123,ADMIN",
            "mario_rossi,cliente123,CUSTOMER",
            "operatore,operatore123,OPERATOR"
    })
    void login_validCredentials_shouldReturnExpectedUserRole(
            String username,
            String password,
            String expectedRole
    ) throws SQLException, InvalidCredentialsException {

        LoginBean loginBean = buildLoginBean(username, password);

        User user = loginController.login(loginBean);

        assertNotNull(user);
        assertEquals(username, user.getUsername());
        assertEquals(expectedRole, user.getRole());
    }

    @ParameterizedTest
    @CsvSource({
            "admin,wrongpass",
            "wronguser,admin123",
            "mario_rossi,wrongpass",
            "operatore,wrongpass"
    })
    void login_invalidCredentials_shouldThrowInvalidCredentialsException(
            String username,
            String password
    ) {

        LoginBean loginBean = buildLoginBean(username, password);

        assertThrows(
                InvalidCredentialsException.class,
                () -> loginController.login(loginBean)
        );
    }

    private LoginBean buildLoginBean(String username, String password) {
        LoginBean loginBean = new LoginBean();
        loginBean.setUsername(username);
        loginBean.setPassword(password);
        return loginBean;
    }
}