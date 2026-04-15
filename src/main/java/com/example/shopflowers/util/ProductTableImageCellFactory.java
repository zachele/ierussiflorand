package com.example.shopflowers.util;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

public final class ProductTableImageCellFactory {

    private ProductTableImageCellFactory() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static <T> Callback<TableColumn<T, String>, TableCell<T, String>> create() {
        return column -> new TableCell<>() {
            private final ImageView imageView = createProductImageView();

            @Override
            protected void updateItem(String imageName, boolean empty) {
                super.updateItem(imageName, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                imageView.setImage(ImageLoader.loadProductImage(imageName));
                setGraphic(imageView);
            }
        };
    }

    private static ImageView createProductImageView() {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(80);
        imageView.setFitHeight(80);
        imageView.setPreserveRatio(true);
        imageView.getStyleClass().add("product-image");

        imageView.setOnMouseEntered(event -> {
            imageView.setScaleX(1.35);
            imageView.setScaleY(1.35);
            imageView.toFront();
        });

        imageView.setOnMouseExited(event -> {
            imageView.setScaleX(1.0);
            imageView.setScaleY(1.0);
        });

        return imageView;
    }
}