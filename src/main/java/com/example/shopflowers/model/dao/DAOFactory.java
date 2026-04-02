package com.example.shopflowers.model.dao;

import com.example.shopflowers.config.AppConfig;
import com.example.shopflowers.config.AppMode;

import java.sql.SQLException;

public final class DAOFactory {

    private DAOFactory() {
    }

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

    public static OrderDAO getOrderDAO() throws SQLException {
        AppMode mode = AppConfig.getMode();

        switch (mode) {
            case DEMO:
                return new OrderMemoryDAO();
            case FILE:
                return new OrderFileDAO();
            case FULL:
            default:
                return new OrderDBDAO();
        }
    }

    public static UserDAO getUserDAO() throws SQLException {
        AppMode mode = AppConfig.getMode();

        switch (mode) {
            case DEMO:
                return new UserMemoryDAO();
            case FILE:
                return new UserFileDAO();
            case FULL:
            default:
                return new UserDBDAO();
        }
    }
    public static OperatorDetailsDAO getOperatorDetailsDAO() throws SQLException {
        AppMode mode = AppConfig.getMode();

        switch (mode) {
            case DEMO:
                return new OperatorDetailsMemoryDAO();
            case FILE:
                return new OperatorDetailsFileDAO();
            case FULL:
            default:
                return new OperatorDetailsDBDAO();
        }
    }
    public static CustomBouquetOrderDAO getCustomBouquetOrderDAO() throws SQLException {
        AppMode mode = AppConfig.getMode();

        switch (mode) {
            case DEMO:
                return new CustomBouquetOrderMemoryDAO();
            case FILE:
                return new CustomBouquetOrderFileDAO();
            case FULL:
            default:
                return new CustomBouquetOrderDBDAO();
        }
    }
}