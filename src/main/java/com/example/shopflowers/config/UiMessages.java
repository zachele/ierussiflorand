package com.example.shopflowers.config;

public final class UiMessages {

    public static final String ORDER_TOTAL_FORMAT = "Totale ordine: € %.2f";

    private UiMessages() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String formatOrderTotal(double total) {
        return String.format(ORDER_TOTAL_FORMAT, total);
    }
}