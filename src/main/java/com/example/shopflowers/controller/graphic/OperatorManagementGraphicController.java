package com.example.shopflowers.controller.graphic;

import com.example.shopflowers.controller.application.ManageOperatorController;
import com.example.shopflowers.exception.InvalidOperatorDataException;
import com.example.shopflowers.exception.UserAlreadyExistsException;
import com.example.shopflowers.model.bean.OperatorBean;
import com.example.shopflowers.model.entity.OperatorFullData;
import com.example.shopflowers.util.SceneNavigator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
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
    private TextField searchField;

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

    private ObservableList<OperatorFullData> masterOperatorList = FXCollections.observableArrayList();
    private FilteredList<OperatorFullData> filteredOperators;

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

        searchField.textProperty().addListener((obs, oldValue, newValue) -> applyFilters());

        operatorTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedOperator = newSelection;

            if (newSelection != null) {
                populateFields(newSelection);
                usernameField.setDisable(true);
                passwordField.setDisable(true);
            }
        });

        loadOperators();
    }

    @FXML
    private void handleCreateOperator() {
        try {
            OperatorBean operatorBean = buildCreateOperatorBean();
            boolean created = manageOperatorController.createOperator(operatorBean);

            if (!created) {
                messageLabel.setText("Compila correttamente tutti i campi richiesti.");
                return;
            }

            clearFields();
            usernameField.setDisable(false);
            passwordField.setDisable(false);
            loadOperators();
            messageLabel.setText("Operatore creato con successo.");

        } catch (UserAlreadyExistsException | InvalidOperatorDataException e) {
            messageLabel.setText(e.getMessage());
        } catch (SQLException e) {
            messageLabel.setText("Errore durante la creazione dell'operatore.");
        }
    }

    @FXML
    private void handleUpdateOperator() {
        if (selectedOperator == null) {
            messageLabel.setText("Seleziona prima un operatore dalla tabella.");
            return;
        }

        try {
            OperatorBean operatorBean = buildUpdateOperatorBean();
            boolean updated = manageOperatorController.updateOperator(operatorBean);

            if (!updated) {
                messageLabel.setText("Compila correttamente tutti i campi richiesti.");
                return;
            }

            loadOperators();
            messageLabel.setText("Operatore aggiornato con successo.");

        } catch (InvalidOperatorDataException e) {
            messageLabel.setText(e.getMessage());
        } catch (SQLException e) {
            messageLabel.setText("Si è verificato un errore durante l'aggiornamento dell'operatore.");
        }
    }

    @FXML
    private void handleDeleteOperator() {
        if (selectedOperator == null) {
            messageLabel.setText("Seleziona prima un operatore dalla tabella.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma eliminazione");
        alert.setHeaderText("Eliminare l'operatore selezionato?");
        alert.setContentText("Verranno rimossi account e dati associati.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            messageLabel.setText("Operazione annullata.");
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
            messageLabel.setText("Si è verificato un errore durante l'eliminazione dell'operatore.");
        }
    }

    @FXML
    private void handleBackToAdmin() {
        try {
            SceneNavigator.goTo(
                    (Stage) messageLabel.getScene().getWindow(),
                    "/com/example/shopflowers/view/admin-product-view.fxml",
                    "Shop Flowers - Gestione Prodotti"
            );
        } catch (IOException e) {
            messageLabel.setText("Si è verificato un errore durante il ritorno all'area amministratore.");
        }
    }

    @FXML
    private void handleLogout() {
        try {
            SceneNavigator.logoutToLogin((Stage) messageLabel.getScene().getWindow());
        } catch (IOException e) {
            messageLabel.setText("Si è verificato un errore durante il logout.");
        }
    }

    private OperatorBean buildCreateOperatorBean() {
        OperatorBean operatorBean = new OperatorBean();
        operatorBean.setName(nameField.getText());
        operatorBean.setSurname(surnameField.getText());
        operatorBean.setUsername(usernameField.getText());
        operatorBean.setPassword(passwordField.getText());
        operatorBean.setSalary(salaryField.getText());
        operatorBean.setContractYear(contractYearField.getText());
        operatorBean.setAnnualHours(annualHoursField.getText());
        return operatorBean;
    }

    private OperatorBean buildUpdateOperatorBean() {
        OperatorBean operatorBean = new OperatorBean();
        operatorBean.setUserId(selectedOperator.getUserId());
        operatorBean.setName(nameField.getText());
        operatorBean.setSurname(surnameField.getText());
        operatorBean.setSalary(salaryField.getText());
        operatorBean.setContractYear(contractYearField.getText());
        operatorBean.setAnnualHours(annualHoursField.getText());
        return operatorBean;
    }

    private void populateFields(OperatorFullData operator) {
        nameField.setText(operator.getName());
        surnameField.setText(operator.getSurname());
        usernameField.setText(operator.getUsername());
        passwordField.clear();
        salaryField.setText(String.valueOf(operator.getSalary()));
        contractYearField.setText(String.valueOf(operator.getContractYear()));
        annualHoursField.setText(String.valueOf(operator.getAnnualHours()));
    }

    private void loadOperators() {
        try {
            List<OperatorFullData> operators = manageOperatorController.getAllOperators();
            masterOperatorList = FXCollections.observableArrayList(operators);
            filteredOperators = new FilteredList<>(masterOperatorList, operator -> true);

            SortedList<OperatorFullData> sortedOperators = new SortedList<>(filteredOperators);
            sortedOperators.comparatorProperty().bind(operatorTable.comparatorProperty());

            operatorTable.setItems(sortedOperators);
            applyFilters();
            operatorTable.refresh();

        } catch (SQLException e) {
            messageLabel.setText("Si è verificato un errore durante il caricamento degli operatori.");
        }
    }

    private void applyFilters() {
        if (filteredOperators == null) {
            return;
        }

        String searchText = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();

        filteredOperators.setPredicate(operator -> {
            if (operator == null) {
                return false;
            }

            return searchText.isBlank()
                    || safe(operator.getName()).contains(searchText)
                    || safe(operator.getSurname()).contains(searchText)
                    || safe(operator.getUsername()).contains(searchText);
        });
    }

    private String safe(String value) {
        return value == null ? "" : value.toLowerCase();
    }

    private void clearFields() {
        nameField.clear();
        surnameField.clear();
        usernameField.clear();
        passwordField.clear();
        salaryField.clear();
        contractYearField.clear();
        annualHoursField.clear();
        searchField.clear();
        operatorTable.getSelectionModel().clearSelection();
    }
}