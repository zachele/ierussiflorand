package com.example.shopflowers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ShopFlowersApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(
                ShopFlowersApplication.class.getResource("/com/example/shopflowers/login-view.fxml")
        );

        Scene scene = new Scene(fxmlLoader.load(), 500, 350);
        stage.setTitle("Shop Flowers - Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}