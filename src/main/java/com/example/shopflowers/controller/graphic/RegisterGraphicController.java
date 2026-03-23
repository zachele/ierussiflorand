package com.example.shopflowers.controller.graphic;

import com.example.shopflowers.ShopFlowersApplication;
import com.example.shopflowers.controller.application.RegisterController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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
        FXMLLoader loader = new FXMLLoader(
                ShopFlowersApplication.class.getResource("/com/example/shopflowers/login-view.fxml")
        );

        Scene scene = new Scene(loader.load(), 500, 350);

        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.setTitle("Shop Flowers - Login");
        stage.setScene(scene);
        stage.show();
    }
}