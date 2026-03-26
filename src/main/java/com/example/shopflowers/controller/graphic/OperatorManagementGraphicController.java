package com.example.shopflowers.controller.graphic;

import com.example.shopflowers.controller.application.ManageOperatorController;
import com.example.shopflowers.model.entity.User;
import com.example.shopflowers.util.SceneNavigator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

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
    private TableView<User> operatorTable;

    @FXML
    private TableColumn<User, Integer> idColumn;

    @FXML
    private TableColumn<User, String> nameColumn;

    @FXML
    private TableColumn<User, String> surnameColumn;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private Label messageLabel;

    private final ManageOperatorController manageOperatorController = new ManageOperatorController();
    private User selectedOperator;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));

        operatorTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) ->
            selectedOperator = newSelection);

        loadOperators();
    }

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

            clearFields();
            loadOperators();
            messageLabel.setText("Operatore creato con successo.");

        } catch (SQLException e) {
            messageLabel.setText("Errore durante la creazione dell'operatore.");
        }
    }

    @FXML
    private void handleDeleteOperator() {
        if (selectedOperator == null) {
            messageLabel.setText("Seleziona prima un operatore.");
            return;
        }

        try {
            manageOperatorController.deleteOperator(selectedOperator.getId());
            selectedOperator = null;
            loadOperators();
            messageLabel.setText("Operatore eliminato con successo.");
        } catch (SQLException e) {
            messageLabel.setText("Errore durante l'eliminazione dell'operatore.");
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

    private void loadOperators() {
        try {
            List<User> operators = manageOperatorController.getAllOperators();
            ObservableList<User> observableOperators = FXCollections.observableArrayList(operators);
            operatorTable.setItems(observableOperators);
        } catch (SQLException e) {
            messageLabel.setText("Errore nel caricamento operatori.");
        }
    }

    private void clearFields() {
        nameField.clear();
        surnameField.clear();
        usernameField.clear();
        passwordField.clear();
        salaryField.clear();
        contractYearField.clear();
        annualHoursField.clear();
    }
}