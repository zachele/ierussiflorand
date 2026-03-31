package com.example.shopflowers.util;

import com.example.shopflowers.model.entity.FlowerProduct;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public final class ProductTableUtils {

    private ProductTableUtils() {}

    public static void configureProductTable(
            TableColumn<FlowerProduct, Integer> idColumn,
            TableColumn<FlowerProduct, String> nameColumn,
            TableColumn<FlowerProduct, Double> priceColumn,
            TableColumn<FlowerProduct, String> colorColumn,
            TableColumn<FlowerProduct, String> varietyColumn,
            TableColumn<FlowerProduct, Integer> stockColumn
    ) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        colorColumn.setCellValueFactory(new PropertyValueFactory<>("color"));
        varietyColumn.setCellValueFactory(new PropertyValueFactory<>("variety"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));
    }

    public static FilteredList<FlowerProduct> loadProductsIntoTable(
            TableView<FlowerProduct> table,
            List<FlowerProduct> products
    ) {
        FilteredList<FlowerProduct> filtered =
                new FilteredList<>(FXCollections.observableArrayList(products), p -> true);

        SortedList<FlowerProduct> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(table.comparatorProperty());

        table.setItems(sorted);
        return filtered;
    }
}