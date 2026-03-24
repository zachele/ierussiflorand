package com.example.shopflowers.controller.graphic;

import com.example.shopflowers.controller.application.RegisterController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import com.example.shopflowers.util.SceneNavigator;

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
        String name = nameField.getText();
        String surname = surnameField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            boolean success = registerController.registerCustomer(name, surname, username, password);

            if (success) {
                messageLabel.setText("Registrazione completata con successo.");
                goToLogin();
            } else {
                messageLabel.setText("Registrazione non valida o username già esistente.");
            }

        } catch (SQLException e) {
            messageLabel.setText("Errore durante la registrazione.");
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

    private void goToLogin() throws IOException {
        SceneNavigator.goTo(
                (Stage) nameField.getScene().getWindow(),
                "/com/example/shopflowers/login-view.fxml",
                "Shop Flowers - Login",
                500,
                350
        );
    }
}