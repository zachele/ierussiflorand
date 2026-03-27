package com.example.shopflowers.controller.graphic;

import com.example.shopflowers.controller.application.ManageOperatorController;
import com.example.shopflowers.model.entity.OperatorFullData;
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

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;


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
    private TableView<OperatorFullData> operatorTable;

    @FXML
    private TableColumn<OperatorFullData, Integer> idColumn;

    @FXML
    private TableColumn<OperatorFullData, String> nameColumn;

    @FXML
    private TableColumn<OperatorFullData, String> surnameColumn;

    @FXML
    private TableColumn<OperatorFullData, String> usernameColumn;

    @FXML
    private TableColumn<OperatorFullData, Double> salaryColumn;

    @FXML
    private TableColumn<OperatorFullData, Integer> contractYearColumn;

    @FXML
    private TableColumn<OperatorFullData, Integer> annualHoursColumn;

    @FXML
    private Label messageLabel;

    private final ManageOperatorController manageOperatorController = new ManageOperatorController();
    private OperatorFullData selectedOperator;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("salary"));
        contractYearColumn.setCellValueFactory(new PropertyValueFactory<>("contractYear"));
        annualHoursColumn.setCellValueFactory(new PropertyValueFactory<>("annualHours"));

        operatorTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedOperator = newSelection;
            if (newSelection != null) {
                nameField.setText(newSelection.getName());
                surnameField.setText(newSelection.getSurname());
                usernameField.setText(newSelection.getUsername());
                passwordField.clear();
                salaryField.setText(String.valueOf(newSelection.getSalary()));
                contractYearField.setText(String.valueOf(newSelection.getContractYear()));
                annualHoursField.setText(String.valueOf(newSelection.getAnnualHours()));

                usernameField.setDisable(true);
                passwordField.setDisable(true);
            }
        });

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
            usernameField.setDisable(false);
            passwordField.setDisable(false);
            loadOperators();
            messageLabel.setText("Operatore creato con successo.");

        } catch (SQLException e) {
            messageLabel.setText("Errore durante la creazione dell'operatore.");
        }
    }

    @FXML
    private void handleUpdateOperator() {
        if (selectedOperator == null) {
            messageLabel.setText("Seleziona prima un operatore.");
            return;
        }

        try {
            boolean updated = manageOperatorController.updateOperator(
                    selectedOperator.getUserId(),
                    nameField.getText(),
                    surnameField.getText(),
                    salaryField.getText(),
                    contractYearField.getText(),
                    annualHoursField.getText()
            );

            if (!updated) {
                messageLabel.setText("Dati non validi per l'aggiornamento.");
                return;
            }

            loadOperators();
            messageLabel.setText("Operatore aggiornato con successo.");

        } catch (SQLException e) {
            messageLabel.setText("Errore durante l'aggiornamento dell'operatore.");
        }
    }

    @FXML
    private void handleDeleteOperator() {
        if (selectedOperator == null) {
            messageLabel.setText("Seleziona prima un operatore.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma eliminazione");
        alert.setHeaderText("Eliminare l'operatore selezionato?");
        alert.setContentText("Verranno rimossi account e dati associati.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            messageLabel.setText("Eliminazione annullata.");
            return;
        }

        try {
            manageOperatorController.deleteOperator(selectedOperator.getUserId());
            selectedOperator = null;
            clearFields();
            usernameField.setDisable(false);
            passwordField.setDisable(false);
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
            List<OperatorFullData> operators = manageOperatorController.getAllOperators();
            ObservableList<OperatorFullData> observableOperators = FXCollections.observableArrayList(operators);
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
