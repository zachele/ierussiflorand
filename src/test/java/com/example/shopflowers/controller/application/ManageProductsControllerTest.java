package com.example.shopflowers.controller.application;

import com.example.shopflowers.config.AppConfig;
import com.example.shopflowers.config.AppMode;
import com.example.shopflowers.model.bean.ProductBean;
import com.example.shopflowers.model.dao.DAOFactory;
import com.example.shopflowers.model.dao.FlowerProductDAO;
import com.example.shopflowers.model.entity.FlowerProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ManageProductsControllerTest {

    private ManageProductsController manageProductsController;
    private FlowerProductDAO flowerProductDAO;

    @BeforeEach
    void setUp() {
        AppConfig.setMode(AppMode.DEMO);
        manageProductsController = new ManageProductsController();
        flowerProductDAO = DAOFactory.getFlowerProductDAO();
    }

    @Test
    void addProduct_validBean_shouldSaveProduct() throws SQLException {
        int initialSize = flowerProductDAO.findAll().size();

        ProductBean productBean = new ProductBean();
        productBean.setName("Peonia Rosa Test");
        productBean.setPrice(8.50);
        productBean.setColor("Rosa");
        productBean.setVariety("Peonia");
        productBean.setStockQuantity(25);

        manageProductsController.addProduct(productBean);

        List<FlowerProduct> products = flowerProductDAO.findAll();

        assertEquals(initialSize + 1, products.size());

        FlowerProduct addedProduct = products.get(products.size() - 1);
        assertEquals("Peonia Rosa Test", addedProduct.getName());
        assertEquals(8.50, addedProduct.getPrice());
        assertEquals("Rosa", addedProduct.getColor());
        assertEquals("Peonia", addedProduct.getVariety());
        assertEquals(25, addedProduct.getStockQuantity());
    }

    @Test
    void updateProduct_existingProduct_shouldUpdateFields() throws SQLException {
        FlowerProduct existingProduct = flowerProductDAO.findAll().get(0);

        ProductBean productBean = new ProductBean();
        productBean.setId(existingProduct.getId());
        productBean.setName("Prodotto Aggiornato Test");
        productBean.setPrice(15.99);
        productBean.setColor("Blu");
        productBean.setVariety("Speciale");
        productBean.setStockQuantity(77);

        manageProductsController.updateProduct(productBean);

        FlowerProduct updatedProduct = flowerProductDAO.findById(existingProduct.getId());

        assertNotNull(updatedProduct);
        assertEquals("Prodotto Aggiornato Test", updatedProduct.getName());
        assertEquals(15.99, updatedProduct.getPrice());
        assertEquals("Blu", updatedProduct.getColor());
        assertEquals("Speciale", updatedProduct.getVariety());
        assertEquals(77, updatedProduct.getStockQuantity());
    }

    @Test
    void deleteProductById_existingProduct_shouldRemoveProduct() throws SQLException {
        ProductBean productBean = new ProductBean();
        productBean.setName("Prodotto Da Eliminare");
        productBean.setPrice(6.30);
        productBean.setColor("Bianco");
        productBean.setVariety("Test");
        productBean.setStockQuantity(10);

        manageProductsController.addProduct(productBean);

        List<FlowerProduct> productsAfterAdd = flowerProductDAO.findAll();
        FlowerProduct addedProduct = productsAfterAdd.get(productsAfterAdd.size() - 1);
        int addedProductId = addedProduct.getId();

        manageProductsController.deleteProductById(addedProductId);

        FlowerProduct deletedProduct = flowerProductDAO.findById(addedProductId);

        assertNull(deletedProduct);
    }
}