package com.example.shopflowers.util;

import com.example.shopflowers.ShopFlowersApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneNavigator {

    private SceneNavigator() {
    }

    public static void goTo(Stage stage, String fxmlPath, String title, double width, double height) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                ShopFlowersApplication.class.getResource(fxmlPath)
        );

        Scene scene = new Scene(loader.load(), width, height);
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    public static void logoutToLogin(Stage stage) throws IOException {
        Session.clearSession();
        goTo(stage, "/com/example/shopflowers/login-view.fxml", "Shop Flowers - Login", 500, 350);
    }
}