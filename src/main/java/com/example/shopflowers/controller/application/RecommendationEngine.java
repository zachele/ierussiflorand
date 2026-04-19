package com.example.shopflowers.controller.application;

import com.example.shopflowers.model.entity.FlowerProduct;
import com.example.shopflowers.model.entity.RecommendationRequest;
import com.example.shopflowers.model.entity.RecommendationResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RecommendationEngine {

    private static final String COLOR_NONE = "NESSUNA";
    private static final String COLOR_MIXED = "MISTO";

    private static final String KEY_ROSA = "rosa";
    private static final String KEY_ROSE = "rose";
    private static final String KEY_ROSSO = "rosso";
    private static final String KEY_ROSSA = "rossa";
    private static final String KEY_GIALLO = "giallo";
    private static final String KEY_MISTO = "misto";
    private static final String KEY_BIANCO = "bianco";

    private static final String KEY_VIVACE = "vivace";
    private static final String KEY_DELICATO = "delicato";
    private static final String KEY_ALLEGRO = "allegro";
    private static final String KEY_SOBRIO = "sobrio";
    private static final String KEY_ELEGANTE = "elegante";
    private static final String KEY_GIGLIO = "giglio";
    private static final String KEY_GERBERA = "gerbera";
    private static final String KEY_SEMPLICE = "semplice";
    private static final String KEY_RAFFINATO = "raffinato";
    private static final String KEY_PREMIUM = "premium";
    private static final String KEY_MARGHERITA = "margherita";

    private static final String[] ROMANTIC_KEYWORDS = {
            KEY_ROSA, KEY_ROSE, KEY_ROSSO, KEY_ROSSA
    };

    private static final String[] DEGREE_KEYWORDS = {
            KEY_ROSSO, KEY_GIALLO, KEY_MISTO, KEY_VIVACE
    };

    private static final String[] THANK_YOU_KEYWORDS = {
            KEY_BIANCO, KEY_GIALLO, KEY_DELICATO
    };

    private static final String[] BIRTHDAY_KEYWORDS = {
            KEY_MISTO, KEY_ROSA, KEY_GIALLO, KEY_ALLEGRO
    };

    private static final String[] CONDOLENCES_KEYWORDS = {
            KEY_BIANCO, KEY_SOBRIO, KEY_ELEGANTE
    };

    private static final String[] ELEGANT_STYLE_KEYWORDS = {
            KEY_BIANCO, KEY_GIGLIO, KEY_ELEGANTE, KEY_RAFFINATO
    };

    private static final String[] CHEERFUL_STYLE_KEYWORDS = {
            KEY_GIALLO, KEY_MISTO, KEY_GERBERA, KEY_VIVACE
    };

    private static final String[] SIMPLE_STYLE_KEYWORDS = {
            KEY_MARGHERITA, KEY_SEMPLICE, KEY_DELICATO
    };

    private static final String[] REFINED_STYLE_KEYWORDS = {
            KEY_GIGLIO, KEY_BIANCO, KEY_PREMIUM, KEY_ROSA
    };

    public List<RecommendationResult> recommend(List<FlowerProduct> products, RecommendationRequest request) {
        List<RecommendationResult> allResults = new ArrayList<>();

        for (FlowerProduct product : products) {
            RecommendationResult result = evaluateProduct(product, request);
            if (result != null) {
                allResults.add(result);
            }
        }

        allResults.sort(Comparator
                .comparingInt(RecommendationResult::getScore).reversed()
                .thenComparing((RecommendationResult r) -> !r.isWithinBudget())
                .thenComparingDouble(r -> Math.abs(r.getProductPrice() - request.getMaxBudget())));

        List<RecommendationResult> withinBudgetResults = allResults.stream()
                .filter(RecommendationResult::isWithinBudget)
                .limit(3)
                .toList();

        if (!withinBudgetResults.isEmpty()) {
            return withinBudgetResults;
        }

        return allResults.stream()
                .limit(3)
                .toList();
    }

    private RecommendationResult evaluateProduct(FlowerProduct product, RecommendationRequest request) {
        if (!isEligibleProduct(product, request)) {
            return null;
        }

        int score = 0;
        List<String> reasons = new ArrayList<>();
        boolean withinBudget = product.getPrice() <= request.getMaxBudget();

        score += evaluateBudget(product, request, reasons);
        score += evaluateColor(product, request, reasons);
        score += evaluateOccasion(product, request, reasons);
        score += evaluateStyle(product, request, reasons);

        String reason = String.join(", ", reasons);
        return new RecommendationResult(product, reason, score, withinBudget);
    }

    private boolean isEligibleProduct(FlowerProduct product, RecommendationRequest request) {
        if (product.getStockQuantity() <= 0) {
            return false;
        }

        if (mustRespectExactColor(request.getPreferredColor())
                && !matchesExactColor(product, request.getPreferredColor())) {
            return false;
        }

        return isWithinAcceptableBudget(product, request);
    }

    private boolean isWithinAcceptableBudget(FlowerProduct product, RecommendationRequest request) {
        return product.getPrice() <= request.getMaxBudget() + 10;
    }

    private int evaluateBudget(FlowerProduct product, RecommendationRequest request, List<String> reasons) {
        if (product.getPrice() <= request.getMaxBudget()) {
            reasons.add("Compatibile con il budget");
            return 3;
        }

        reasons.add("Leggermente sopra il budget");
        return 1;
    }

    private int evaluateColor(FlowerProduct product, RecommendationRequest request, List<String> reasons) {
        if (matchesColor(product, request.getPreferredColor())) {
            reasons.add("Colore coerente con la preferenza");
            return 2;
        }
        return 0;
    }

    private int evaluateOccasion(FlowerProduct product, RecommendationRequest request, List<String> reasons) {
        if (matchesOccasion(product, request.getOccasion())) {
            reasons.add("Adatto all'occasione");
            return 3;
        }
        return 0;
    }

    private int evaluateStyle(FlowerProduct product, RecommendationRequest request, List<String> reasons) {
        if (matchesStyle(product, request.getStyle())) {
            reasons.add("Stile coerente con la richiesta");
            return 2;
        }
        return 0;
    }

    private boolean matchesColor(FlowerProduct product, String preferredColor) {
        if (preferredColor == null || preferredColor.isBlank() || preferredColor.equalsIgnoreCase(COLOR_NONE)) {
            return true;
        }

        return product.getColor() != null
                && product.getColor().toLowerCase().contains(preferredColor.toLowerCase());
    }

    private boolean matchesOccasion(FlowerProduct product, String occasion) {
        String name = safe(product.getName());
        String variety = safe(product.getVariety());
        String color = safe(product.getColor());

        return switch (occasion.toUpperCase()) {
            case "ANNIVERSARIO", "ROMANTICO" ->
                    containsOneOf(name, variety, color, ROMANTIC_KEYWORDS);
            case "LAUREA" ->
                    containsOneOf(name, variety, color, DEGREE_KEYWORDS);
            case "RINGRAZIAMENTO" ->
                    containsOneOf(name, variety, color, THANK_YOU_KEYWORDS);
            case "COMPLEANNO" ->
                    containsOneOf(name, variety, color, BIRTHDAY_KEYWORDS);
            case "CONDOGLIANZE" ->
                    containsOneOf(name, variety, color, CONDOLENCES_KEYWORDS);
            default -> false;
        };
    }

    private boolean matchesStyle(FlowerProduct product, String style) {
        String name = safe(product.getName());
        String variety = safe(product.getVariety());
        String color = safe(product.getColor());

        return switch (style.toUpperCase()) {
            case "ROMANTICO" ->
                    containsOneOf(name, variety, color, KEY_ROSA, KEY_ROSE, KEY_ROSSO);
            case "ELEGANTE" ->
                    containsOneOf(name, variety, color, ELEGANT_STYLE_KEYWORDS);
            case "ALLEGRO" ->
                    containsOneOf(name, variety, color, CHEERFUL_STYLE_KEYWORDS);
            case "SEMPLICE" ->
                    containsOneOf(name, variety, color, SIMPLE_STYLE_KEYWORDS);
            case "RAFFINATO" ->
                    containsOneOf(name, variety, color, REFINED_STYLE_KEYWORDS);
            default -> false;
        };
    }

    private boolean containsOneOf(String name, String variety, String color, String... values) {
        for (String value : values) {
            String normalizedValue = value.toLowerCase();
            if (name.contains(normalizedValue)
                    || variety.contains(normalizedValue)
                    || color.contains(normalizedValue)) {
                return true;
            }
        }
        return false;
    }

    private boolean mustRespectExactColor(String preferredColor) {
        return preferredColor != null
                && !preferredColor.isBlank()
                && !preferredColor.equalsIgnoreCase(COLOR_NONE)
                && !preferredColor.equalsIgnoreCase(COLOR_MIXED);
    }

    private boolean matchesExactColor(FlowerProduct product, String preferredColor) {
        return product.getColor() != null
                && product.getColor().toLowerCase().contains(preferredColor.toLowerCase());
    }

    private String safe(String value) {
        return value == null ? "" : value.toLowerCase();
    }
}