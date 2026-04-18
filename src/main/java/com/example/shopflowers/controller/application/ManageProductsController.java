package com.example.shopflowers.controller.application;

import com.example.shopflowers.model.bean.ProductBean;
import com.example.shopflowers.model.dao.DAOFactory;
import com.example.shopflowers.model.dao.FlowerProductDAO;
import com.example.shopflowers.model.entity.FlowerProduct;

import java.sql.SQLException;

public class ManageProductsController {

    private final FlowerProductDAO flowerProductDAO;

    public ManageProductsController() {
            this.flowerProductDAO = DAOFactory.getFlowerProductDAO();
    }

    public void addProduct(ProductBean productBean) throws SQLException {
        flowerProductDAO.save(toFlowerProduct(productBean));
    }

    public void updateProduct(ProductBean productBean) throws SQLException {
        flowerProductDAO.update(toFlowerProduct(productBean));
    }

    public void deleteProductById(int id) throws SQLException {
        flowerProductDAO.deleteById(id);
    }

    private FlowerProduct toFlowerProduct(ProductBean productBean) {
        return new FlowerProduct(
                productBean.getId(),
                productBean.getName(),
                productBean.getPrice(),
                productBean.getColor(),
                productBean.getVariety(),
                productBean.getStockQuantity(),
                productBean.getImageName()
        );
    }
}