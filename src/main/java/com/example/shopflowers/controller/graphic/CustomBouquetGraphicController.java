package com.example.shopflowers.controller.graphic;

import com.example.shopflowers.controller.application.CustomBouquetController;
import com.example.shopflowers.controller.application.CustomerCartController;
import com.example.shopflowers.model.entity.CustomBouquet;
import com.example.shopflowers.model.entity.CustomBouquetItem;
import com.example.shopflowers.model.entity.FlowerProduct;
import com.example.shopflowers.util.SceneNavigator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class CustomBouquetGraphicController {

    @FXML
    private ComboBox<String> sizeComboBox;

    @FXML
    private ComboBox<String> packagingComboBox;

    @FXML
    private CheckBox cardCheckBox;

    @FXML
    private CheckBox vaseCheckBox;

    @FXML
    private TableView<FlowerProduct> flowerTable;

    @FXML
    private TableColumn<FlowerProduct, String> flowerNameColumn;

    @FXML
    private TableColumn<FlowerProduct, Double> flowerPriceColumn;

    @FXML
    private TableColumn<FlowerProduct, String> flowerColorColumn;

    @FXML
    private TableColumn<FlowerProduct, String> flowerVarietyColumn;

    @FXML
    private TableColumn<FlowerProduct, Integer> flowerStockColumn;

    @FXML
    private TextField quantityField;

    @FXML
    private TableView<CustomBouquetItem> bouquetTable;

    @FXML
    private TableColumn<CustomBouquetItem, String> bouquetNameColumn;

    @FXML
    private TableColumn<CustomBouquetItem, Integer> bouquetQuantityColumn;

    @FXML
    private TableColumn<CustomBouquetItem, Double> bouquetUnitPriceColumn;

    @FXML
    private TableColumn<CustomBouquetItem, Double> bouquetSubtotalColumn;

    @FXML
    private Label totalLabel;

    @FXML
    private Label messageLabel;

    @FXML
    private TextField budgetField;

    private final CustomBouquetController customBouquetController = new CustomBouquetController();
    private CustomerCartController customerCartController;

    private FlowerProduct selectedFlower;
    private CustomBouquetItem selectedBouquetItem;

    @FXML
    public void initialize() {
        try {
            customerCartController = CustomerCatalogGraphicController.getSharedCartController();

            sizeComboBox.setItems(FXCollections.observableArrayList("PICCOLO", "MEDIO", "GRANDE"));
            packagingComboBox.setItems(FXCollections.observableArrayList("STANDARD", "PREMIUM"));

            flowerNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            flowerPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
            flowerColorColumn.setCellValueFactory(new PropertyValueFactory<>("color"));
            flowerVarietyColumn.setCellValueFactory(new PropertyValueFactory<>("variety"));
            flowerStockColumn.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));

            bouquetNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
            bouquetQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            bouquetUnitPriceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
            bouquetSubtotalColumn.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

            flowerTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) ->
                    selectedFlower = newSelection);

            bouquetTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) ->
                    selectedBouquetItem = newSelection);

            loadFlowers();
            refreshBouquetTable();
        } catch (Exception e) {
            if (messageLabel != null) {
                messageLabel.setText("");
            }
        }
    }

    @FXML
    private void handleAddFlower() {
        if (selectedFlower == null) {
            messageLabel.setText("Seleziona prima un fiore.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityField.getText());
        } catch (NumberFormatException e) {
            messageLabel.setText("Inserisci una quantità valida.");
            return;
        }

        if (budgetField.getText() != null && !budgetField.getText().isBlank()) {
            try {
                Double.parseDouble(budgetField.getText());
            } catch (NumberFormatException e) {
                messageLabel.setText("Inserisci un budget valido.");
                return;
            }
        }

        applyBouquetConfiguration();

        boolean added = customBouquetController.addFlowerToBouquet(selectedFlower, quantity);
        if (!added) {
            messageLabel.setText("Quantità non valida o stock insufficiente.");
            return;
        }

        refreshBouquetTable();
        quantityField.clear();

        if (customBouquetController.isWithinBudget()) {
            messageLabel.setText("Fiore aggiunto al bouquet.");
        } else {
            messageLabel.setText(String.format(
                    "Budget superato di € %.2f. Modifica la composizione prima di confermare.",
                    customBouquetController.getExceededAmount()
            ));
        }
    }

    @FXML
    private void handleRemoveFlower() {
        if (selectedBouquetItem == null) {
            messageLabel.setText("Seleziona prima un fiore dalla composizione.");
            return;
        }

        boolean removed = customBouquetController.removeFlowerFromBouquet(selectedBouquetItem);
        if (!removed) {
            messageLabel.setText("Errore durante la rimozione del fiore.");
            return;
        }

        selectedBouquetItem = null;
        refreshBouquetTable();
        messageLabel.setText("Fiore rimosso dal bouquet.");
    }

    @FXML
    private void handleResetBouquet() {
        if (customBouquetController.getCurrentItems().isEmpty()) {
            messageLabel.setText("Il bouquet è già vuoto.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma reset");
        alert.setHeaderText("Vuoi azzerare il bouquet personalizzato?");
        alert.setContentText("Tutti gli elementi inseriti verranno rimossi.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            messageLabel.setText("Reset annullato.");
            return;
        }

        customBouquetController.resetBouquet();
        selectedBouquetItem = null;
        sizeComboBox.setValue(null);
        packagingComboBox.setValue(null);
        cardCheckBox.setSelected(false);
        vaseCheckBox.setSelected(false);
        quantityField.clear();
        budgetField.clear();
        refreshBouquetTable();
        messageLabel.setText("Bouquet azzerato.");
    }

    @FXML
    private void handleConfirmCustomBouquet() {
        if (sizeComboBox.getValue() == null || packagingComboBox.getValue() == null) {
            messageLabel.setText("Seleziona dimensione e confezione.");
            return;
        }

        if (customBouquetController.getCurrentItems().isEmpty()) {
            messageLabel.setText("Aggiungi almeno un fiore al bouquet.");
            return;
        }

        if (budgetField.getText() != null && !budgetField.getText().isBlank()) {
            try {
                Double.parseDouble(budgetField.getText());
            } catch (NumberFormatException e) {
                messageLabel.setText("Inserisci un budget valido.");
                return;
            }
        }

        applyBouquetConfiguration();

        if (!customBouquetController.isWithinBudget()) {
            messageLabel.setText(String.format(
                    "Non puoi confermare: il bouquet supera il budget di € %.2f.",
                    customBouquetController.getExceededAmount()
            ));
            return;
        }

        CustomBouquet bouquet = customBouquetController.buildBouquet();

        for (CustomBouquetItem item : bouquet.getItems()) {
            customerCartController.addToCart(item.getFlowerProduct(), item.getQuantity());
        }

        customBouquetController.resetBouquet();
        selectedBouquetItem = null;
        refreshBouquetTable();
        sizeComboBox.setValue(null);
        packagingComboBox.setValue(null);
        cardCheckBox.setSelected(false);
        vaseCheckBox.setSelected(false);
        budgetField.clear();
        totalLabel.setText(String.format("Totale bouquet: € %.2f", 0.0));
        messageLabel.setText("Bouquet entro budget aggiunto al carrello.");
    }

    @FXML
    private void handleBackToCatalog() {
        try {
            SceneNavigator.goTo(
                    (Stage) messageLabel.getScene().getWindow(),
                    "/com/example/shopflowers/catalog-view.fxml",
                    "Shop Flowers - Catalogo Cliente"
            );
        } catch (IOException e) {
            messageLabel.setText("Errore nel ritorno al catalogo.");
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

    private void applyBouquetConfiguration() {
        Double budget = null;

        if (budgetField.getText() != null && !budgetField.getText().isBlank()) {
            try {
                budget = Double.parseDouble(budgetField.getText());
            } catch (NumberFormatException e) {
                messageLabel.setText("Inserisci un budget valido.");
                return;
            }
        }

        customBouquetController.configureBouquet(
                sizeComboBox.getValue(),
                packagingComboBox.getValue(),
                cardCheckBox.isSelected(),
                vaseCheckBox.isSelected(),
                budget
        );
    }
    private void loadFlowers() {
        try {
            List<FlowerProduct> flowers = customBouquetController.getAvailableFlowers();
            flowerTable.setItems(FXCollections.observableArrayList(flowers));
        } catch (Exception e) {
            messageLabel.setText("Errore nel caricamento dei fiori.");
        }
    }

    private void refreshBouquetTable() {
        bouquetTable.setItems(FXCollections.observableArrayList(customBouquetController.getCurrentItems()));
        totalLabel.setText(String.format("Totale bouquet: € %.2f", customBouquetController.getCurrentTotal()));
    }
}