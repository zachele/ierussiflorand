package com.example.shopflowers.controller.graphic;

import com.example.shopflowers.boundary.RegisterBoundary;
import com.example.shopflowers.exception.UserAlreadyExistsException;
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

    private final RegisterBoundary registerBoundary = new RegisterBoundary();

    @FXML
    private void handleRegister() {
        try {
            boolean success = registerBoundary.registerCustomer(
                    nameField.getText(),
                    surnameField.getText(),
                    usernameField.getText(),
                    passwordField.getText()
            );

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

    private void goToLogin() throws IOException {
        SceneNavigator.goTo(
                (Stage) nameField.getScene().getWindow(),
                "/com/example/shopflowers/login-view.fxml",
                "Shop Flowers - Login"
        );
    }
}