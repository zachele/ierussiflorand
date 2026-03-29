package com.example.shopflowers.controller.graphic;

import com.example.shopflowers.controller.application.BrowseCatalogController;
import com.example.shopflowers.controller.application.ManageProductsController;
import com.example.shopflowers.model.entity.FlowerProduct;
import com.example.shopflowers.util.SceneNavigator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

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
    private TextField searchField;

    @FXML
    private ComboBox<String> colorFilterComboBox;

    @FXML
    private CheckBox availableOnlyCheckBox;

    @FXML
    private Label messageLabel;

    private final BrowseCatalogController browseCatalogController = new BrowseCatalogController();
    private final ManageProductsController manageProductsController = new ManageProductsController();

    private ObservableList<FlowerProduct> masterProductList = FXCollections.observableArrayList();
    private FilteredList<FlowerProduct> filteredProducts;

    private FlowerProduct selectedProduct;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        colorColumn.setCellValueFactory(new PropertyValueFactory<>("color"));
        varietyColumn.setCellValueFactory(new PropertyValueFactory<>("variety"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));

        colorFilterComboBox.setItems(FXCollections.observableArrayList(
                "Tutti", "Rosso", "Bianco", "Rosa", "Giallo", "Misto"
        ));
        colorFilterComboBox.setValue("Tutti");

        searchField.textProperty().addListener((obs, oldValue, newValue) -> applyFilters());
        colorFilterComboBox.valueProperty().addListener((obs, oldValue, newValue) -> applyFilters());
        availableOnlyCheckBox.selectedProperty().addListener((obs, oldValue, newValue) -> applyFilters());

        productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedProduct = newSelection;
            if (newSelection != null) {
                populateFields(newSelection);
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
            messageLabel.setText("Seleziona prima un prodotto.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma eliminazione");
        alert.setHeaderText("Eliminare il prodotto selezionato?");
        alert.setContentText("Questa operazione non può essere annullata.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            messageLabel.setText("Eliminazione annullata.");
            return;
        }

        try {
            manageProductsController.deleteProductById(selectedProduct.getId());
            clearFields();
            loadProducts();
            selectedProduct = null;
            messageLabel.setText("Prodotto eliminato con successo.");
        } catch (SQLException e) {
            messageLabel.setText("Errore durante l'eliminazione del prodotto.");
        }
    }

    private void loadProducts() {
        try {
            List<FlowerProduct> products = browseCatalogController.getAllProducts();
            masterProductList = FXCollections.observableArrayList(products);
            filteredProducts = new FilteredList<>(masterProductList, product -> true);

            SortedList<FlowerProduct> sortedProducts = new SortedList<>(filteredProducts);
            sortedProducts.comparatorProperty().bind(productTable.comparatorProperty());

            productTable.setItems(sortedProducts);
            applyFilters();
            productTable.refresh();

        } catch (SQLException e) {
            messageLabel.setText("Errore nel caricamento prodotti: " + e.getMessage());
        }
    }

    private void applyFilters() {
        if (filteredProducts == null) {
            return;
        }

        String searchText = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
        String selectedColor = colorFilterComboBox.getValue();
        boolean availableOnly = availableOnlyCheckBox.isSelected();

        filteredProducts.setPredicate(product -> {
            if (product == null) {
                return false;
            }

            boolean matchesSearch = searchText.isBlank()
                    || safe(product.getName()).contains(searchText)
                    || safe(product.getColor()).contains(searchText)
                    || safe(product.getVariety()).contains(searchText);

            boolean matchesColor = selectedColor == null
                    || selectedColor.equalsIgnoreCase("Tutti")
                    || safe(product.getColor()).contains(selectedColor.toLowerCase());

            boolean matchesAvailability = !availableOnly || product.getStockQuantity() > 0;

            return matchesSearch && matchesColor && matchesAvailability;
        });
    }

    private String safe(String value) {
        return value == null ? "" : value.toLowerCase();
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

    @FXML
    private void handleGoToOperatorManagement() {
        try {
            SceneNavigator.goTo(
                    (Stage) productTable.getScene().getWindow(),
                    "/com/example/shopflowers/operator-management-view.fxml",
                    "Shop Flowers - Gestione Operatori"
            );
        } catch (IOException e) {
            messageLabel.setText("Errore nell'apertura della gestione operatori.");
        }
    }

    @FXML
    private void handleGoToStatistics() {
        try {
            SceneNavigator.goTo(
                    (Stage) productTable.getScene().getWindow(),
                    "/com/example/shopflowers/statistics-view.fxml",
                    "Shop Flowers - Statistiche Vendite"
            );
        } catch (IOException e) {
            messageLabel.setText("Errore nell'apertura delle statistiche.");
        }
    }
}