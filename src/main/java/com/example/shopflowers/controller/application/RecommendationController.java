package com.example.shopflowers.controller.application;

import com.example.shopflowers.model.bean.RecommendationRequestBean;
import com.example.shopflowers.model.dao.DAOFactory;
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
        try {
            this.flowerProductDAO = DAOFactory.getFlowerProductDAO();
        } catch (SQLException e) {
            throw new IllegalStateException("Impossibile inizializzare la DAO dei prodotti.", e);
        }
        this.recommendationEngine = new RecommendationEngine();
    }

    public List<RecommendationResult> getRecommendations(RecommendationRequestBean requestBean) throws SQLException {
        List<FlowerProduct> products = flowerProductDAO.findAll();
        RecommendationRequest request = toRecommendationRequest(requestBean);
        return recommendationEngine.recommend(products, request);
    }

    private RecommendationRequest toRecommendationRequest(RecommendationRequestBean requestBean) {
        return new RecommendationRequest(
                requestBean.getOccasion(),
                requestBean.getStyle(),
                requestBean.getMaxBudget(),
                requestBean.getPreferredColor()
        );
    }
}