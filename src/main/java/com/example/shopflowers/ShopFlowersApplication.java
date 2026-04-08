package com.example.shopflowers;

import com.example.shopflowers.config.AppConfig;
import com.example.shopflowers.config.AppMode;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ShopFlowersApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        AppConfig.setMode(AppMode.DEMO); // cambia in DEMO o FILE per testare le altre modalità

        FXMLLoader fxmlLoader = new FXMLLoader(
                ShopFlowersApplication.class.getResource("/com/example/shopflowers/login-view.fxml")
        );

        Scene scene = new Scene(fxmlLoader.load(), 1500, 800);
        stage.setTitle("Ierussi Florand - Login");
        stage.setScene(scene);
        stage.show();
    }
}