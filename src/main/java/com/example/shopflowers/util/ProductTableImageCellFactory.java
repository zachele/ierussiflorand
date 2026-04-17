package com.example.shopflowers.util;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

public final class ProductTableImageCellFactory {

    private static final double IMAGE_SIZE = 80.0;
    private static final double HOVER_SCALE = 1.35;

    private ProductTableImageCellFactory() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static <T> Callback<TableColumn<T, String>, TableCell<T, String>> create() {
        return column -> new TableCell<>() {

            private final ImageView imageView = createProductImageView();

            {
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                setGraphic(null);

                setOnMouseEntered(event -> applyZoom(true));
                setOnMouseExited(event -> applyZoom(false));
            }

            @Override
            protected void updateItem(String imageName, boolean empty) {
                super.updateItem(imageName, empty);

                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    applyZoom(false);
                    return;
                }

                imageView.setImage(ImageLoader.loadProductImage(imageName));
                setGraphic(imageView);
            }

            private void applyZoom(boolean zoomed) {
                double scale = zoomed ? HOVER_SCALE : 1.0;
                imageView.setScaleX(scale);
                imageView.setScaleY(scale);

                if (zoomed) {
                    toFront();
                    imageView.toFront();
                }
            }
        };
    }

    private static ImageView createProductImageView() {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(IMAGE_SIZE);
        imageView.setFitHeight(IMAGE_SIZE);
        imageView.setPreserveRatio(true);
        imageView.getStyleClass().add("product-image");
        imageView.setPickOnBounds(true);
        return imageView;
    }
}