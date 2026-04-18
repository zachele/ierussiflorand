package com.example.shopflowers.model.dao;

import com.example.shopflowers.config.AppConfig;
import com.example.shopflowers.config.AppMode;

public final class DAOFactory {

    private static final FlowerProductDAO FLOWER_PRODUCT_MEMORY_DAO = new FlowerProductMemoryDAO();
    private static final OrderDAO ORDER_MEMORY_DAO = new OrderMemoryDAO();
    private static final UserDAO USER_MEMORY_DAO = new UserMemoryDAO();
    private static final OperatorDetailsDAO OPERATOR_DETAILS_MEMORY_DAO = new OperatorDetailsMemoryDAO();
    private static final CustomBouquetOrderDAO CUSTOM_BOUQUET_ORDER_MEMORY_DAO = new CustomBouquetOrderMemoryDAO();

    private DAOFactory() {
    }

    public static FlowerProductDAO getFlowerProductDAO() {
        AppMode mode = AppConfig.getMode();

        return switch (mode) {
            case DEMO -> FLOWER_PRODUCT_MEMORY_DAO;
            case FILE -> new FlowerProductFileDAO();
            case FULL -> new FlowerProductDBDAO();
        };
    }

    public static OrderDAO getOrderDAO() {
        AppMode mode = AppConfig.getMode();

        return switch (mode) {
            case DEMO -> ORDER_MEMORY_DAO;
            case FILE -> new OrderFileDAO();
            case FULL -> new OrderDBDAO();
        };
    }

    public static UserDAO getUserDAO() {
        AppMode mode = AppConfig.getMode();

        return switch (mode) {
            case DEMO -> USER_MEMORY_DAO;
            case FILE -> new UserFileDAO();
            case FULL -> new UserDBDAO();
        };
    }

    public static OperatorDetailsDAO getOperatorDetailsDAO() {
        AppMode mode = AppConfig.getMode();

        return switch (mode) {
            case DEMO -> OPERATOR_DETAILS_MEMORY_DAO;
            case FILE -> new OperatorDetailsFileDAO();
            case FULL -> new OperatorDetailsDBDAO();
        };
    }

    public static CustomBouquetOrderDAO getCustomBouquetOrderDAO() {
        AppMode mode = AppConfig.getMode();

        return switch (mode) {
            case DEMO -> CUSTOM_BOUQUET_ORDER_MEMORY_DAO;
            case FILE -> new CustomBouquetOrderFileDAO();
            case FULL -> new CustomBouquetOrderDBDAO();
        };
    }
}