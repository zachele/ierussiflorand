package com.example.shopflowers.util;

import com.example.shopflowers.model.entity.FlowerProduct;
import javafx.collections.transformation.FilteredList;

public final class ProductFilterUtils {

    private ProductFilterUtils() {
    }

    public static boolean matchesProductFilters(
            FlowerProduct product,
            String searchText,
            String selectedColor,
            boolean availableOnly
    ) {
        if (product == null) {
            return false;
        }

        String normalizedSearch = normalize(searchText);
        String normalizedSelectedColor = normalize(selectedColor);

        boolean matchesSearch = normalizedSearch.isBlank()
                || normalize(product.getName()).contains(normalizedSearch)
                || normalize(product.getColor()).contains(normalizedSearch)
                || normalize(product.getVariety()).contains(normalizedSearch);

        boolean matchesColor = normalizedSelectedColor.isBlank()
                || "tutti".equals(normalizedSelectedColor)
                || normalize(product.getColor()).contains(normalizedSelectedColor);

        boolean matchesAvailability = !availableOnly || product.getStockQuantity() > 0;

        return matchesSearch && matchesColor && matchesAvailability;
    }

    public static void applyProductFilters(
            FilteredList<FlowerProduct> filteredProducts,
            String searchText,
            String selectedColor,
            boolean availableOnly
    ) {
        if (filteredProducts == null) {
            return;
        }

        filteredProducts.setPredicate(product ->
                matchesProductFilters(product, searchText, selectedColor, availableOnly)
        );
    }

    public static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }
}