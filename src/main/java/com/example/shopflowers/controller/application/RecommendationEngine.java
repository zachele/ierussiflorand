package com.example.shopflowers.controller.application;

import com.example.shopflowers.model.entity.FlowerProduct;
import com.example.shopflowers.model.entity.RecommendationRequest;
import com.example.shopflowers.model.entity.RecommendationResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RecommendationEngine {

    public List<RecommendationResult> recommend(List<FlowerProduct> products, RecommendationRequest request) {
        List<RecommendationResult> allResults = new ArrayList<>();

        for (FlowerProduct product : products) {
            if (product.getStockQuantity() <= 0) {
                continue;
            }

            int score = 0;
            List<String> reasons = new ArrayList<>();
            boolean withinBudget = product.getPrice() <= request.getMaxBudget();

            if (withinBudget) {
                score += 3;
                reasons.add("Compatibile con il budget");
            } else if (product.getPrice() <= request.getMaxBudget() + 10) {
                score += 1;
                reasons.add("Leggermente sopra il budget");
            } else {
                continue;
            }

            if (matchesColor(product, request.getPreferredColor())) {
                score += 2;
                reasons.add("Colore coerente con la preferenza");
            }

            if (matchesOccasion(product, request.getOccasion())) {
                score += 3;
                reasons.add("Adatto all'occasione");
            }

            if (matchesStyle(product, request.getStyle())) {
                score += 2;
                reasons.add("Stile coerente con la richiesta");
            }

            if (score > 0) {
                String reason = String.join(", ", reasons);
                allResults.add(new RecommendationResult(product, reason, score, withinBudget));
            }
        }

        allResults.sort(Comparator
                .comparingInt(RecommendationResult::getScore).reversed()
                .thenComparing((RecommendationResult r) -> !r.isWithinBudget())
                .thenComparingDouble(r -> Math.abs(r.getProductPrice() - request.getMaxBudget())));

        List<RecommendationResult> withinBudgetResults = allResults.stream()
                .filter(RecommendationResult::isWithinBudget)
                .limit(3)
                .collect(Collectors.toList());

        if (!withinBudgetResults.isEmpty()) {
            return withinBudgetResults;
        }

        return allResults.stream().limit(3).collect(Collectors.toList());
    }

    private boolean matchesColor(FlowerProduct product, String preferredColor) {
        if (preferredColor == null || preferredColor.isBlank() || preferredColor.equalsIgnoreCase("NESSUNA")) {
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
                    containsOneOf(name, variety, color, "rosa", "rose", "rosso", "rossa");
            case "LAUREA" ->
                    containsOneOf(name, variety, color, "rosso", "giallo", "misto", "vivace");
            case "RINGRAZIAMENTO" ->
                    containsOneOf(name, variety, color, "bianco", "giallo", "delicato");
            case "COMPLEANNO" ->
                    containsOneOf(name, variety, color, "misto", "rosa", "giallo", "allegro");
            case "CONDOGLIANZE" ->
                    containsOneOf(name, variety, color, "bianco", "sobrio", "elegante");
            default -> false;
        };
    }

    private boolean matchesStyle(FlowerProduct product, String style) {
        String name = safe(product.getName());
        String variety = safe(product.getVariety());
        String color = safe(product.getColor());

        return switch (style.toUpperCase()) {
            case "ROMANTICO" ->
                    containsOneOf(name, variety, color, "rosa", "rose", "rosso");
            case "ELEGANTE" ->
                    containsOneOf(name, variety, color, "bianco", "giglio", "elegante", "raffinato");
            case "ALLEGRO" ->
                    containsOneOf(name, variety, color, "giallo", "misto", "gerbera", "vivace");
            case "SEMPLICE" ->
                    containsOneOf(name, variety, color, "margherita", "semplice", "delicato");
            case "RAFFINATO" ->
                    containsOneOf(name, variety, color, "giglio", "bianco", "premium", "rosa");
            default -> false;
        };
    }

    private boolean containsOneOf(String name, String variety, String color, String... values) {
        for (String value : values) {
            String v = value.toLowerCase();
            if (name.contains(v) || variety.contains(v) || color.contains(v)) {
                return true;
            }
        }
        return false;
    }

    private String safe(String value) {
        return value == null ? "" : value.toLowerCase();
    }
}