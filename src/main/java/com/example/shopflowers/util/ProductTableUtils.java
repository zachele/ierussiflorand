package com.example.shopflowers.util;

import com.example.shopflowers.model.entity.FlowerProduct;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public final class ProductTableUtils {

    private ProductTableUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void configureProductColumns(
            TableColumn<FlowerProduct, Integer> idColumn,
            TableColumn<FlowerProduct, String> nameColumn,
            TableColumn<FlowerProduct, Double> priceColumn,
            TableColumn<FlowerProduct, String> colorColumn,
            TableColumn<FlowerProduct, String> varietyColumn,
            TableColumn<FlowerProduct, Integer> stockColumn
    ) {
        if (idColumn != null) {
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        }

        if (nameColumn != null) {
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        }

        if (priceColumn != null) {
            priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        }

        if (colorColumn != null) {
            colorColumn.setCellValueFactory(new PropertyValueFactory<>("color"));
        }

        if (varietyColumn != null) {
            varietyColumn.setCellValueFactory(new PropertyValueFactory<>("variety"));
        }

        if (stockColumn != null) {
            stockColumn.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));
        }
    }

    public static void configureProductTableWithImage(
            TableColumn<FlowerProduct, String> imageColumn,
            TableColumn<FlowerProduct, Integer> idColumn,
            TableColumn<FlowerProduct, String> nameColumn,
            TableColumn<FlowerProduct, Double> priceColumn,
            TableColumn<FlowerProduct, String> colorColumn,
            TableColumn<FlowerProduct, String> varietyColumn,
            TableColumn<FlowerProduct, Integer> stockColumn
    ) {
        if (imageColumn != null) {
            imageColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getImageName())
            );
            imageColumn.setCellFactory(ProductTableImageCellFactory.create());
        }

        configureProductColumns(
                idColumn,
                nameColumn,
                priceColumn,
                colorColumn,
                varietyColumn,
                stockColumn
        );
    }

    public static FilteredList<FlowerProduct> loadProductsIntoTable(
            TableView<FlowerProduct> table,
            List<FlowerProduct> products
    ) {
        FilteredList<FlowerProduct> filteredProducts =
                new FilteredList<>(FXCollections.observableArrayList(products), product -> true);

        SortedList<FlowerProduct> sortedProducts = new SortedList<>(filteredProducts);
        sortedProducts.comparatorProperty().bind(table.comparatorProperty());

        table.setItems(sortedProducts);
        return filteredProducts;
    }
}