package com.example.shopflowers.util;

import com.example.shopflowers.config.ResourcePaths;
import javafx.scene.image.Image;

import java.io.InputStream;

public final class ImageLoader {

    private ImageLoader() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static Image loadProductImage(String imageName) {
        String resolvedImageName = (imageName == null || imageName.isBlank())
                ? ResourcePaths.DEFAULT_PRODUCT_IMAGE
                : imageName;

        InputStream inputStream = ImageLoader.class.getResourceAsStream(
                ResourcePaths.PRODUCT_IMAGES_DIRECTORY + resolvedImageName
        );

        if (inputStream == null) {
            inputStream = ImageLoader.class.getResourceAsStream(
                    ResourcePaths.PRODUCT_IMAGES_DIRECTORY + ResourcePaths.DEFAULT_PRODUCT_IMAGE
            );
        }

        return inputStream != null ? new Image(inputStream) : null;
    }
}