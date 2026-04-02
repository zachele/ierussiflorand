package com.example.shopflowers.model.dao;

import com.example.shopflowers.config.AppConfig;
import com.example.shopflowers.config.AppMode;

import java.sql.SQLException;

public class DAOFactory {

    public static FlowerProductDAO getFlowerProductDAO() throws SQLException {
        AppMode mode = AppConfig.getMode();

        switch (mode) {
            case DEMO:
                return new FlowerProductMemoryDAO();
            case FILE:
                return new FlowerProductFileDAO();
            case FULL:
            default:
                return new FlowerProductDBDAO();
        }
    }
}