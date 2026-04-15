package com.example.shopflowers.util;

import com.example.shopflowers.config.ResourcePaths;
import javafx.scene.image.Image;

import java.io.InputStream;

public final class ImageLoader {

    private ImageLoader() {
        throw new UnsupportedOperationException("Utility class");
    }

    @SuppressWarnings("java:S1075")
    public static Image loadProductImage(String imageName) {
        String resolvedImageName = resolveImageName(imageName);

        InputStream inputStream = openImageStream(resolvedImageName);

        if (inputStream == null) {
            inputStream = openImageStream(ResourcePaths.DEFAULT_PRODUCT_IMAGE);
        }

        return inputStream != null ? new Image(inputStream) : null;
    }

    private static String resolveImageName(String imageName) {
        return (imageName == null || imageName.isBlank())
                ? ResourcePaths.DEFAULT_PRODUCT_IMAGE
                : imageName;
    }

    private static InputStream openImageStream(String fileName) {
        return ImageLoader.class.getResourceAsStream(
                ResourcePaths.PRODUCT_IMAGES_DIRECTORY + fileName
        );
    }
}