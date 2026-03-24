package com.example.shopflowers.util;

import com.example.shopflowers.ShopFlowersApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneNavigator {

    private SceneNavigator() {
    }

    public static void goTo(Stage stage, String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                ShopFlowersApplication.class.getResource(fxmlPath)
        );

        Scene scene = new Scene(loader.load());
        stage.setTitle(title);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public static void logoutToLogin(Stage stage) throws IOException {
        Session.clearSession();
        goTo(stage, "/com/example/shopflowers/login-view.fxml", "Shop Flowers - Login");
    }
}