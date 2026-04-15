package com.example.shopflowers.controller.graphic;

import com.example.shopflowers.controller.application.BrowseCatalogController;
import com.example.shopflowers.controller.application.ManageProductsController;
import com.example.shopflowers.model.bean.ProductBean;
import com.example.shopflowers.model.entity.FlowerProduct;
import com.example.shopflowers.util.ProductFilterUIUtils;
import com.example.shopflowers.util.ProductFilterUtils;
import com.example.shopflowers.util.ProductTableUtils;
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
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AdminProductGraphicController {

    private static final List<String> AVAILABLE_IMAGES = List.of(
            "ranuncolo_yellow.png",
            "ranuncolo_salmon.png",
            "ranuncolo_pink.png",
            "ranuncolo_blue.png",
            "ranuncolo_white.png",
            "ranuncolo_red.png",
            "hanoi_blue.png",
            "hanoi_white.png",
            "rose_red.png",
            "rose_white.png",
            "rose_pink.png",
            "rose_blue.png",
            "mimosa.png",
            "tulip_red.png",
            "tulip_violet.png",
            "tulip_white.png",
            "tulip_yellow.png",
            "ninfea.png",
            "orchid_purple.png",
            "orchid_white.png",
            "sunflower_yellow.png",
            "lily_white.png",
            "mixed_bouquet.png"
    );

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
    private ComboBox<String> imageComboBox;

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

    private FilteredList<FlowerProduct> filteredProducts;
    private FlowerProduct selectedProduct;

    @FXML
    public void initialize() {
        ProductTableUtils.configureProductColumns(
                idColumn,
                nameColumn,
                priceColumn,
                colorColumn,
                varietyColumn,
                stockColumn
        );

        configureImageSelection();

        ProductFilterUIUtils.configureColorFilter(colorFilterComboBox);
        ProductFilterUIUtils.bindFilterListeners(
                searchField,
                colorFilterComboBox,
                availableOnlyCheckBox,
                this::applyFilters
        );

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
            ProductBean productBean = buildProductBeanFromFields();
            manageProductsController.addProduct(productBean);

            messageLabel.setText("Prodotto aggiunto con successo.");
            clearFields();
            loadProducts();

        } catch (NumberFormatException e) {
            messageLabel.setText("Dati non validi. Controlla prezzo e quantità disponibile.");
        } catch (SQLException e) {
            messageLabel.setText("Si è verificato un errore durante il salvataggio del prodotto.");
        }
    }

    @FXML
    private void handleUpdateProduct() {
        if (selectedProduct == null) {
            messageLabel.setText("Seleziona prima un prodotto dalla tabella.");
            return;
        }

        try {
            ProductBean productBean = buildProductBeanFromFields();
            productBean.setId(selectedProduct.getId());

            manageProductsController.updateProduct(productBean);

            messageLabel.setText("Prodotto aggiornato con successo.");
            clearFields();
            loadProducts();
            selectedProduct = null;

        } catch (NumberFormatException e) {
            messageLabel.setText("Dati non validi. Controlla prezzo e quantità disponibile.");
        } catch (SQLException e) {
            messageLabel.setText("Si è verificato un errore durante l'aggiornamento del prodotto.");
        }
    }

    @FXML
    private void handleDeleteProduct() {
        if (selectedProduct == null) {
            messageLabel.setText("Seleziona prima un prodotto dalla tabella.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma eliminazione");
        alert.setHeaderText("Eliminare il prodotto selezionato?");
        alert.setContentText("Questa operazione non può essere annullata.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            messageLabel.setText("Operazione annullata.");
            return;
        }

        try {
            manageProductsController.deleteProductById(selectedProduct.getId());
            clearFields();
            loadProducts();
            selectedProduct = null;
            messageLabel.setText("Prodotto eliminato con successo.");
        } catch (SQLException e) {
            messageLabel.setText("Si è verificato un errore durante l'eliminazione del prodotto.");
        }
    }

    private void configureImageSelection() {
        imageComboBox.setItems(FXCollections.observableArrayList(AVAILABLE_IMAGES));
        imageComboBox.setPromptText("Seleziona immagine prodotto");
    }

    private ProductBean buildProductBeanFromFields() {
        ProductBean productBean = new ProductBean();
        productBean.setName(nameField.getText());
        productBean.setPrice(Double.parseDouble(priceField.getText()));
        productBean.setColor(colorField.getText());
        productBean.setVariety(varietyField.getText());
        productBean.setStockQuantity(Integer.parseInt(stockField.getText()));
        productBean.setImageName(imageComboBox.getValue());
        return productBean;
    }

    private void loadProducts() {
        try {
            List<FlowerProduct> products = browseCatalogController.getAllProducts();
            ObservableList<FlowerProduct> masterProductList = FXCollections.observableArrayList(products);
            filteredProducts = new FilteredList<>(masterProductList, product -> true);

            SortedList<FlowerProduct> sortedProducts = new SortedList<>(filteredProducts);
            sortedProducts.comparatorProperty().bind(productTable.comparatorProperty());

            productTable.setItems(sortedProducts);
            applyFilters();
            productTable.refresh();

        } catch (SQLException e) {
            messageLabel.setText("Si è verificato un errore durante il caricamento dei prodotti.");
        }
    }

    private void applyFilters() {
        ProductFilterUtils.applyProductFilters(
                filteredProducts,
                searchField.getText(),
                colorFilterComboBox.getValue(),
                availableOnlyCheckBox.isSelected()
        );
    }

    private void populateFields(FlowerProduct product) {
        nameField.setText(product.getName());
        priceField.setText(String.valueOf(product.getPrice()));
        colorField.setText(product.getColor());
        varietyField.setText(product.getVariety());
        stockField.setText(String.valueOf(product.getStockQuantity()));
        imageComboBox.setValue(product.getImageName());
    }

    private void clearFields() {
        nameField.clear();
        priceField.clear();
        colorField.clear();
        varietyField.clear();
        stockField.clear();
        imageComboBox.setValue(null);
        productTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleGoToOperatorManagement() {
        try {
            SceneNavigator.goTo(
                    (Stage) productTable.getScene().getWindow(),
                    "/com/example/shopflowers/view/operator-management-view.fxml",
                    "Shop Flowers - Gestione Operatori"
            );
        } catch (IOException e) {
            messageLabel.setText("Si è verificato un errore durante l'apertura della gestione operatori.");
        }
    }

    @FXML
    private void handleGoToStatistics() {
        try {
            SceneNavigator.goTo(
                    (Stage) productTable.getScene().getWindow(),
                    "/com/example/shopflowers/view/statistics-view.fxml",
                    "Shop Flowers - Statistiche Vendite"
            );
        } catch (IOException e) {
            messageLabel.setText("Si è verificato un errore durante l'apertura delle statistiche.");
        }
    }
    @FXML
    @SuppressWarnings("unused")
    private void handleLogout() {
        try {
            SceneNavigator.logoutToLogin((Stage) productTable.getScene().getWindow());
        } catch (IOException e) {
            messageLabel.setText("Si è verificato un errore durante il logout.");
        }
    }
}