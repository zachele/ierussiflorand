package com.example.shopflowers.controller.graphic;

import com.example.shopflowers.controller.application.BrowseCatalogController;
import com.example.shopflowers.controller.application.ManageProductsController;
import com.example.shopflowers.model.entity.FlowerProduct;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.SQLException;
import java.util.List;
import javafx.stage.Stage;
import java.io.IOException;
import com.example.shopflowers.util.SceneNavigator;

public class AdminProductGraphicController {

    @FXML
    private TableView<FlowerProduct> productTable;

    @FXML
    private TableColumn<FlowerProduct, Integer> idColumn;

    @FXML
    private TableColumn<FlowerProduct, String> nameColumn;

    @FXML
    private TableColumn<FlowerProduct, Double> priceColumn;

    @FXML
    private TableColumn<FlowerProduct, String> colorColumn;

    @FXML
    private TableColumn<FlowerProduct, String> varietyColumn;

    @FXML
    private TableColumn<FlowerProduct, Integer> stockColumn;

    @FXML
    private TextField nameField;

    @FXML
    private TextField priceField;

    @FXML
    private TextField colorField;

    @FXML
    private TextField varietyField;

    @FXML
    private TextField stockField;

    @FXML
    private Label messageLabel;

    private final BrowseCatalogController browseCatalogController = new BrowseCatalogController();
    private final ManageProductsController manageProductsController = new ManageProductsController();

    private FlowerProduct selectedProduct;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        colorColumn.setCellValueFactory(new PropertyValueFactory<>("color"));
        varietyColumn.setCellValueFactory(new PropertyValueFactory<>("variety"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));

        productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedProduct = newSelection;
                populateFields(selectedProduct);
            }
        });

        loadProducts();
    }

    @FXML
    private void handleAddProduct() {
        try {
            String name = nameField.getText();
            double price = Double.parseDouble(priceField.getText());
            String color = colorField.getText();
            String variety = varietyField.getText();
            int stock = Integer.parseInt(stockField.getText());

            FlowerProduct product = new FlowerProduct(name, price, color, variety, stock);
            manageProductsController.addProduct(product);

            messageLabel.setText("Prodotto aggiunto correttamente.");
            clearFields();
            loadProducts();

        } catch (NumberFormatException e) {
            messageLabel.setText("Prezzo o stock non validi.");
        } catch (SQLException e) {
            messageLabel.setText("Errore durante il salvataggio nel database: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateProduct() {
        if (selectedProduct == null) {
            messageLabel.setText("Seleziona prima un prodotto da modificare.");
            return;
        }

        try {
            selectedProduct.setName(nameField.getText());
            selectedProduct.setPrice(Double.parseDouble(priceField.getText()));
            selectedProduct.setColor(colorField.getText());
            selectedProduct.setVariety(varietyField.getText());
            selectedProduct.setStockQuantity(Integer.parseInt(stockField.getText()));

            manageProductsController.updateProduct(selectedProduct);

            messageLabel.setText("Prodotto aggiornato correttamente.");
            clearFields();
            loadProducts();
            selectedProduct = null;

        } catch (NumberFormatException e) {
            messageLabel.setText("Prezzo o stock non validi.");
        } catch (SQLException e) {
            messageLabel.setText("Errore durante l'aggiornamento del prodotto: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteProduct() {
        if (selectedProduct == null) {
            messageLabel.setText("Seleziona prima un prodotto da eliminare.");
            return;
        }

        try {
            manageProductsController.deleteProductById(selectedProduct.getId());

            messageLabel.setText("Prodotto eliminato correttamente.");
            clearFields();
            loadProducts();
            selectedProduct = null;

        } catch (SQLException e) {
            messageLabel.setText("Errore durante l'eliminazione del prodotto: " + e.getMessage());
        }
    }

    private void loadProducts() {
        try {
            List<FlowerProduct> products = browseCatalogController.getAllProducts();

            ObservableList<FlowerProduct> observableProducts = FXCollections.observableArrayList(products);
            productTable.setItems(observableProducts);
            productTable.refresh();

        } catch (SQLException e) {
            messageLabel.setText("Errore nel caricamento prodotti: " + e.getMessage());
        }
    }

    private void populateFields(FlowerProduct product) {
        nameField.setText(product.getName());
        priceField.setText(String.valueOf(product.getPrice()));
        colorField.setText(product.getColor());
        varietyField.setText(product.getVariety());
        stockField.setText(String.valueOf(product.getStockQuantity()));
    }

    private void clearFields() {
        nameField.clear();
        priceField.clear();
        colorField.clear();
        varietyField.clear();
        stockField.clear();
        productTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleLogout() {
        try {
            SceneNavigator.logoutToLogin((Stage) productTable.getScene().getWindow());
        } catch (IOException e) {
            messageLabel.setText("Errore durante il logout.");
        }
    }
}