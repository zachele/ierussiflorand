package com.example.shopflowers.controller.graphic;

import com.example.shopflowers.controller.application.RegisterController;
import com.example.shopflowers.exception.UserAlreadyExistsException;
import com.example.shopflowers.model.bean.RegisterUserBean;
import com.example.shopflowers.util.AlertUtils;
import com.example.shopflowers.util.SceneNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class RegisterGraphicController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField surnameField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    private final RegisterController registerController = new RegisterController();

    @FXML
    private void handleRegister() {
        RegisterUserBean registerUserBean = buildRegisterUserBean();

        try {
            boolean success = registerController.registerCustomer(registerUserBean);

            if (success) {
                AlertUtils.showInfo(
                        "Registrazione",
                        "Registrazione completata con successo."
                );
                goToLogin();
            } else {
                AlertUtils.showWarning(
                        "Registrazione non riuscita",
                        "Controlla i dati inseriti."
                );
            }

        } catch (UserAlreadyExistsException e) {
            AlertUtils.showWarning(
                    "Registrazione non riuscita",
                    e.getMessage()
            );
        } catch (SQLException e) {
            messageLabel.setText("Si è verificato un errore durante la registrazione.");
        } catch (IOException e) {
            messageLabel.setText("Errore nel ritorno alla login.");
        }
    }

    @FXML
    private void handleBackToLogin() {
        try {
            goToLogin();
        } catch (IOException e) {
            messageLabel.setText("Errore nel ritorno alla login.");
        }
    }

    private RegisterUserBean buildRegisterUserBean() {
        RegisterUserBean registerUserBean = new RegisterUserBean();
        registerUserBean.setName(nameField.getText());
        registerUserBean.setSurname(surnameField.getText());
        registerUserBean.setUsername(usernameField.getText());
        registerUserBean.setPassword(passwordField.getText());
        return registerUserBean;
    }

    private void goToLogin() throws IOException {
        SceneNavigator.goTo(
                (Stage) nameField.getScene().getWindow(),
                "/com/example/shopflowers/login-view.fxml",
                "Shop Flowers - Login"
        );
    }
}