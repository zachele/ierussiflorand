package com.example.shopflowers.util;

import com.example.shopflowers.model.entity.FlowerProduct;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;
import java.util.List;

public final class ProductTableUtils {

    private static final String PRODUCT_IMAGES_PATH = "/com/example/shopflowers/images/products/";
    private static final String DEFAULT_IMAGE = "default_product.png";

    private ProductTableUtils() {
    }

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

    public static void configureProductTableWithImage(
            TableColumn<FlowerProduct, String> imageColumn,
            TableColumn<FlowerProduct, Integer> idColumn,
            TableColumn<FlowerProduct, String> nameColumn,
            TableColumn<FlowerProduct, Double> priceColumn,
            TableColumn<FlowerProduct, String> colorColumn,
            TableColumn<FlowerProduct, String> varietyColumn,
            TableColumn<FlowerProduct, Integer> stockColumn
    ) {
        configureProductTable(
                idColumn,
                nameColumn,
                priceColumn,
                colorColumn,
                varietyColumn,
                stockColumn
        );

        imageColumn.setCellValueFactory(new PropertyValueFactory<>("imageName"));
        imageColumn.setCellFactory(column -> new TableCell<>() {
            private final ImageView imageView = new ImageView();

            {
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageView.setPreserveRatio(true);
                imageView.getStyleClass().add("product-image");

                setOnMouseEntered(event -> {
                    imageView.setScaleX(1.35);
                    imageView.setScaleY(1.35);
                    imageView.toFront();
                });

                setOnMouseExited(event -> {
                    imageView.setScaleX(1.0);
                    imageView.setScaleY(1.0);
                });
            }

            @Override
            protected void updateItem(String imageName, boolean empty) {
                super.updateItem(imageName, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                Image image = loadProductImage(imageName);
                imageView.setImage(image);
                setGraphic(imageView);
            }
        });
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

    private static Image loadProductImage(String imageName) {
        String resolvedImageName = (imageName == null || imageName.isBlank())
                ? DEFAULT_IMAGE
                : imageName;

        InputStream inputStream = ProductTableUtils.class.getResourceAsStream(
                PRODUCT_IMAGES_PATH + resolvedImageName
        );

        if (inputStream == null) {
            inputStream = ProductTableUtils.class.getResourceAsStream(
                    PRODUCT_IMAGES_PATH + DEFAULT_IMAGE
            );
        }

        if (inputStream == null) {
            return null;
        }

        return new Image(inputStream);
    }
}