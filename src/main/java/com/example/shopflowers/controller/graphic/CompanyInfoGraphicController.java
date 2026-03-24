package com.example.shopflowers.controller.graphic;

import com.example.shopflowers.ShopFlowersApplication;
import com.example.shopflowers.util.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class CompanyInfoGraphicController {

    @FXML
    private Label messageLabel;

    @FXML
    private void handleBackToCatalog() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    ShopFlowersApplication.class.getResource("/com/example/shopflowers/catalog-view.fxml")
            );

            Scene scene = new Scene(loader.load(), 900, 650);

            Stage stage = (Stage) messageLabel.getScene().getWindow();
            stage.setTitle("Shop Flowers - Catalogo Cliente");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            messageLabel.setText("Errore nel ritorno al catalogo.");
        }
    }

    @FXML
    private void handleLogout() {
        try {
            Session.clearSession();

            FXMLLoader loader = new FXMLLoader(
                    ShopFlowersApplication.class.getResource("/com/example/shopflowers/login-view.fxml")
            );

            Scene scene = new Scene(loader.load(), 500, 350);

            Stage stage = (Stage) messageLabel.getScene().getWindow();
            stage.setTitle("Shop Flowers - Login");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            messageLabel.setText("Errore durante il logout.");
        }
    }
}