package com.example.shopflowers.controller.graphic;

import com.example.shopflowers.controller.application.ManageOperatorController;
import com.example.shopflowers.util.SceneNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class OperatorManagementGraphicController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField surnameField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField salaryField;

    @FXML
    private TextField contractYearField;

    @FXML
    private TextField annualHoursField;

    @FXML
    private Label messageLabel;

    private final ManageOperatorController manageOperatorController = new ManageOperatorController();

    @FXML
    private void handleCreateOperator() {
        try {
            boolean created = manageOperatorController.createOperator(
                    nameField.getText(),
                    surnameField.getText(),
                    usernameField.getText(),
                    passwordField.getText(),
                    salaryField.getText(),
                    contractYearField.getText(),
                    annualHoursField.getText()
            );

            if (!created) {
                messageLabel.setText("Dati non validi o username già esistente.");
                return;
            }

            nameField.clear();
            surnameField.clear();
            usernameField.clear();
            passwordField.clear();
            salaryField.clear();
            contractYearField.clear();
            annualHoursField.clear();

            messageLabel.setText("Operatore creato con successo.");

        } catch (SQLException e) {
            messageLabel.setText("Errore durante la creazione dell'operatore.");
        }
    }

    @FXML
    private void handleBackToAdmin() {
        try {
            SceneNavigator.goTo(
                    (Stage) messageLabel.getScene().getWindow(),
                    "/com/example/shopflowers/admin-product-view.fxml",
                    "Shop Flowers - Gestione Prodotti"
            );
        } catch (IOException e) {
            messageLabel.setText("Errore nel ritorno all'area admin.");
        }
    }

    @FXML
    private void handleLogout() {
        try {
            SceneNavigator.logoutToLogin((Stage) messageLabel.getScene().getWindow());
        } catch (IOException e) {
            messageLabel.setText("Errore durante il logout.");
        }
    }
}
