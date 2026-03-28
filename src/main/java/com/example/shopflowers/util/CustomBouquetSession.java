package com.example.shopflowers.util;

import com.example.shopflowers.model.entity.CustomBouquet;

public class CustomBouquetSession {

    private static CustomBouquet currentBouquet;

    private CustomBouquetSession() {
    }

    public static void setCurrentBouquet(CustomBouquet bouquet) {
        currentBouquet = bouquet;
    }

    public static CustomBouquet getCurrentBouquet() {
        return currentBouquet;
    }

    public static boolean hasBouquet() {
        return currentBouquet != null;
    }

    public static void clear() {
        currentBouquet = null;
    }
}
