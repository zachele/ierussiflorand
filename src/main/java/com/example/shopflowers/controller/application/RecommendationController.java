package com.example.shopflowers.controller.application;

import com.example.shopflowers.model.dao.FlowerProductDAO;
import com.example.shopflowers.model.entity.FlowerProduct;
import com.example.shopflowers.model.entity.RecommendationRequest;
import com.example.shopflowers.model.entity.RecommendationResult;

import java.sql.SQLException;
import java.util.List;

public class RecommendationController {

    private final FlowerProductDAO flowerProductDAO;
    private final RecommendationEngine recommendationEngine;

    public RecommendationController() {
        this.flowerProductDAO = new FlowerProductDAO();
        this.recommendationEngine = new RecommendationEngine();
    }

    public List<RecommendationResult> getRecommendations(RecommendationRequest request) throws SQLException {
        List<FlowerProduct> products = flowerProductDAO.findAll();
        return recommendationEngine.recommend(products, request);
    }
}
