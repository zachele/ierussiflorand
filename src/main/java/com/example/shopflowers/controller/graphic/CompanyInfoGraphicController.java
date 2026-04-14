package com.example.shopflowers.controller.graphic;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.IOException;
import com.example.shopflowers.util.SceneNavigator;

public class CompanyInfoGraphicController {

    @FXML
    private Label messageLabel;

    @FXML
    private void handleBackToCatalog() {
        try {
            SceneNavigator.goTo(
                    (Stage) messageLabel.getScene().getWindow(),
                    "/com/example/shopflowers/view/catalog-view.fxml",
                    "Shop Flowers - Catalogo Cliente"
            );

        } catch (IOException e) {
            messageLabel.setText("Si è verificato un errore durante il ritorno al catalogo.");
        }
    }
}