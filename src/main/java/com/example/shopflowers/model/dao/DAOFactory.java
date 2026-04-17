package com.example.shopflowers.model.dao;

import com.example.shopflowers.config.AppConfig;
import com.example.shopflowers.config.AppMode;

import java.sql.SQLException;

public final class DAOFactory {

    private DAOFactory() {
    }

    public static FlowerProductDAO getFlowerProductDAO() throws SQLException {
        AppMode mode = AppConfig.getMode();

        return switch (mode) {
            case DEMO -> new FlowerProductMemoryDAO();
            case FILE -> new FlowerProductFileDAO();
            case FULL -> new FlowerProductDBDAO();
        };
    }

    public static OrderDAO getOrderDAO() {
        AppMode mode = AppConfig.getMode();

        return switch (mode) {
            case DEMO -> new OrderMemoryDAO();
            case FILE -> new OrderFileDAO();
            case FULL -> new OrderDBDAO();
        };
    }

    public static UserDAO getUserDAO() {
        AppMode mode = AppConfig.getMode();

        return switch (mode) {
            case DEMO -> new UserMemoryDAO();
            case FILE -> new UserFileDAO();
            case FULL -> new UserDBDAO();
        };
    }

    public static OperatorDetailsDAO getOperatorDetailsDAO() throws SQLException {
        AppMode mode = AppConfig.getMode();

        return switch (mode) {
            case DEMO -> new OperatorDetailsMemoryDAO();
            case FILE -> new OperatorDetailsFileDAO();
            case FULL -> new OperatorDetailsDBDAO();
        };
    }

    public static CustomBouquetOrderDAO getCustomBouquetOrderDAO() {
        AppMode mode = AppConfig.getMode();

        return switch (mode) {
            case DEMO -> new CustomBouquetOrderMemoryDAO();
            case FILE -> new CustomBouquetOrderFileDAO();
            case FULL -> new CustomBouquetOrderDBDAO();
        };
    }
}