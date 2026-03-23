package com.example.shopflowers.controller.graphic;

import com.example.shopflowers.controller.application.BrowseCatalogController;
import com.example.shopflowers.model.entity.FlowerProduct;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.List;

public class CatalogGraphicController {

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

    private final BrowseCatalogController browseCatalogController = new BrowseCatalogController();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        colorColumn.setCellValueFactory(new PropertyValueFactory<>("color"));
        varietyColumn.setCellValueFactory(new PropertyValueFactory<>("variety"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));

        loadProducts();
    }

    private void loadProducts() {
        try {
            List<FlowerProduct> products = browseCatalogController.getAllProducts();
            ObservableList<FlowerProduct> observableProducts = FXCollections.observableArrayList(products);
            productTable.setItems(observableProducts);
        } catch (SQLException e) {
        }
    }
}