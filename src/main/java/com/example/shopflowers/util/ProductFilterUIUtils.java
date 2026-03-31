package com.example.shopflowers.util;

import javafx.collections.FXCollections;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public final class ProductFilterUIUtils {

    private ProductFilterUIUtils() {
    }

    public static void configureColorFilter(ComboBox<String> colorFilterComboBox) {
        colorFilterComboBox.setItems(FXCollections.observableArrayList(
                "Tutti", "Rosso", "Bianco", "Rosa", "Giallo", "Misto"
        ));
        colorFilterComboBox.setValue("Tutti");
    }

    public static void bindFilterListeners(
            TextField searchField,
            ComboBox<String> colorFilterComboBox,
            CheckBox availableOnlyCheckBox,
            Runnable applyFiltersAction
    ) {
        searchField.textProperty().addListener((obs, oldValue, newValue) -> applyFiltersAction.run());
        colorFilterComboBox.valueProperty().addListener((obs, oldValue, newValue) -> applyFiltersAction.run());
        availableOnlyCheckBox.selectedProperty().addListener((obs, oldValue, newValue) -> applyFiltersAction.run());
    }
}