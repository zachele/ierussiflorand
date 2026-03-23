package com.example.shopflowers.controller.graphic;

import com.example.shopflowers.controller.application.LoginController;
import com.example.shopflowers.model.entity.User;
import com.example.shopflowers.ShopFlowersApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

import com.example.shopflowers.util.Session;

public class LoginGraphicController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    private final LoginController loginController = new LoginController();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            User user = loginController.login(username, password);

            if (user == null) {
                messageLabel.setText("Credenziali non valide.");
                return;
            }
            Session.setSession(user.getUsername(), user.getRole());
            switch (user.getRole()) {
                case "ADMIN" -> openView("/com/example/shopflowers/admin-product-view.fxml", "Shop Flowers - Admin");
                case "CUSTOMER" -> openView("/com/example/shopflowers/catalog-view.fxml", "Shop Flowers - Catalogo");
                case "OPERATOR" -> openView("/com/example/shopflowers/operator-view.fxml", "Shop Flowers - Operatore");
                default -> messageLabel.setText("Ruolo non riconosciuto.");
            }

        } catch (SQLException e) {
            messageLabel.setText("Errore durante il login.");
        } catch (IOException e) {
            messageLabel.setText("Errore nel caricamento della schermata.");
        }
    }

    private void openView(String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(ShopFlowersApplication.class.getResource(fxmlPath));
        Scene scene = new Scene(loader.load());

        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    private void handleGoToRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    ShopFlowersApplication.class.getResource("/com/example/shopflowers/register-view.fxml")
            );

            Scene scene = new Scene(loader.load(), 550, 420);

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setTitle("Shop Flowers - Registrazione");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            messageLabel.setText("Errore nell'apertura della registrazione.");
        }
    }
}