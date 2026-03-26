package com.example.shopflowers.controller.application;

import com.example.shopflowers.model.dao.StatisticsDAO;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class StatisticsController {

    private final StatisticsDAO statisticsDAO;

    public StatisticsController() {
        this.statisticsDAO = new StatisticsDAO();
    }

    public Map<String, String> getStatistics() throws SQLException {
        Map<String, String> statistics = new LinkedHashMap<>();

        statistics.put("Totale ordini", String.valueOf(statisticsDAO.getTotalOrders()));
        statistics.put("Totale ricavi", String.format("€ %.2f", statisticsDAO.getTotalRevenue()));
        statistics.put("Ordini consegnati", String.valueOf(statisticsDAO.getDeliveredOrders()));
        statistics.put("Ordini attivi", String.valueOf(statisticsDAO.getActiveOrders()));
        statistics.put("Clienti registrati", String.valueOf(statisticsDAO.getTotalClients()));
        statistics.put("Operatori registrati", String.valueOf(statisticsDAO.getTotalOperators()));
        statistics.put("Prodotto più venduto", statisticsDAO.getMostSoldProduct());

        return statistics;
    }
}