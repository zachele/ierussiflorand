package com.example.shopflowers.controller.graphic;

import com.example.shopflowers.controller.application.CustomerCartController;
import com.example.shopflowers.controller.application.RecommendationController;
import com.example.shopflowers.exception.InvalidQuantityException;
import com.example.shopflowers.model.bean.RecommendationRequestBean;
import com.example.shopflowers.model.entity.RecommendationResult;
import com.example.shopflowers.util.CartSession;
import com.example.shopflowers.util.SceneNavigator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

public class RecommendationGraphicController {

    private static final String PRODUCT_IMAGES_PATH = "/com/example/shopflowers/images/products/";
    private static final String DEFAULT_IMAGE = "default_product.png";

    @FXML
    private ComboBox<String> occasionComboBox;

    @FXML
    private ComboBox<String> styleComboBox;

    @FXML
    private ComboBox<String> colorComboBox;

    @FXML
    private TextField budgetField;

    @FXML
    private TextField quantityField;

    @FXML
    private TableView<RecommendationResult> recommendationTable;

    @FXML
    private TableColumn<RecommendationResult, String> imageColumn;

    @FXML
    private TableColumn<RecommendationResult, String> nameColumn;

    @FXML
    private TableColumn<RecommendationResult, Double> priceColumn;

    @FXML
    private TableColumn<RecommendationResult, String> colorColumn;

    @FXML
    private TableColumn<RecommendationResult, String> varietyColumn;

    @FXML
    private TableColumn<RecommendationResult, String> budgetColumn;

    @FXML
    private TableColumn<RecommendationResult, String> reasonColumn;

    @FXML
    private Label messageLabel;

    private final RecommendationController recommendationController = new RecommendationController();

    private RecommendationResult selectedRecommendation;

    @FXML
    public void initialize() {
        occasionComboBox.setItems(FXCollections.observableArrayList(
                "COMPLEANNO", "ANNIVERSARIO", "LAUREA", "RINGRAZIAMENTO", "CONDOGLIANZE", "ROMANTICO"
        ));

        styleComboBox.setItems(FXCollections.observableArrayList(
                "ROMANTICO", "ELEGANTE", "ALLEGRO", "SEMPLICE", "RAFFINATO"
        ));

        colorComboBox.setItems(FXCollections.observableArrayList(
                "ROSSO", "BIANCO", "ROSA", "GIALLO", "MISTO", "NESSUNA"
        ));

        configureRecommendationTable();

        recommendationTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) ->
                selectedRecommendation = newValue);
    }

    @FXML
    private void handleFindRecommendations() {
        String occasion = occasionComboBox.getValue();
        String style = styleComboBox.getValue();
        String color = colorComboBox.getValue();
        String budgetText = budgetField.getText();

        if (occasion == null || style == null || color == null || budgetText == null || budgetText.isBlank()) {
            messageLabel.setText("Compila tutti i campi dell'assistente.");
            return;
        }

        double budget;
        try {
            budget = Double.parseDouble(budgetText);
        } catch (NumberFormatException e) {
            messageLabel.setText("Inserisci un budget valido.");
            return;
        }

        try {
            RecommendationRequestBean requestBean = buildRecommendationRequestBean(
                    occasion,
                    style,
                    color,
                    budget
            );

            List<RecommendationResult> results = recommendationController.getRecommendations(requestBean);
            recommendationTable.setItems(FXCollections.observableArrayList(results));

            if (results.isEmpty()) {
                messageLabel.setText("Nessuna proposta disponibile con questi criteri.");
                return;
            }

            boolean allWithinBudget = results.stream().allMatch(RecommendationResult::isWithinBudget);

            if (allWithinBudget) {
                messageLabel.setText("Mostrate le migliori proposte compatibili con il budget.");
            } else {
                messageLabel.setText("Nessuna proposta perfettamente entro budget: mostrate le alternative più vicine.");
            }

        } catch (SQLException e) {
            messageLabel.setText("Errore nel caricamento delle proposte.");
        }
    }

    @FXML
    private void handleAddRecommendedToCart() {
        if (selectedRecommendation == null) {
            messageLabel.setText("Seleziona prima una proposta.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityField.getText());
        } catch (NumberFormatException e) {
            messageLabel.setText("Inserisci una quantità valida.");
            return;
        }

        if (quantity <= 0) {
            messageLabel.setText("La quantità deve essere maggiore di zero.");
            return;
        }

        try {
            boolean added = getCartController().addToCart(selectedRecommendation.getProduct(), quantity);

            if (!added) {
                messageLabel.setText("Operazione non riuscita. Quantità richiesta non disponibile.");
                return;
            }

            messageLabel.setText("Prodotto consigliato aggiunto al carrello.");
            quantityField.clear();

        } catch (InvalidQuantityException e) {
            messageLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleBackToCatalog() {
        try {
            SceneNavigator.goTo(
                    (Stage) messageLabel.getScene().getWindow(),
                    "/com/example/shopflowers/catalog-view.fxml",
                    "Shop Flowers - Catalogo Cliente"
            );
        } catch (IOException e) {
            messageLabel.setText("Si è verificato un errore durante il ritorno al catalogo.");
        }
    }

    @FXML
    private void handleLogout() {
        try {
            SceneNavigator.logoutToLogin((Stage) messageLabel.getScene().getWindow());
        } catch (IOException e) {
            messageLabel.setText("Si è verificato un errore durante il logout.");
        }
    }

    private void configureRecommendationTable() {
        imageColumn.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createStringBinding(
                        () -> cellData.getValue() != null
                                && cellData.getValue().getProduct() != null
                                ? cellData.getValue().getProduct().getImageName()
                                : null
                )
        );

        imageColumn.setCellFactory(column -> new TableCell<>() {
            private final ImageView imageView = new ImageView();

            {
                imageView.setFitWidth(80);
                imageView.setFitHeight(80);
                imageView.setPreserveRatio(true);
                imageView.getStyleClass().add("product-image");

                setOnMouseEntered(event -> {
                    imageView.setScaleX(1.35);
                    imageView.setScaleY(1.35);
                    imageView.toFront();
                });

                setOnMouseExited(event -> {
                    imageView.setScaleX(1.0);
                    imageView.setScaleY(1.0);
                });
            }

            @Override
            protected void updateItem(String imageName, boolean empty) {
                super.updateItem(imageName, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                imageView.setImage(loadProductImage(imageName));
                setGraphic(imageView);
            }
        });

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("productPrice"));
        colorColumn.setCellValueFactory(new PropertyValueFactory<>("productColor"));
        varietyColumn.setCellValueFactory(new PropertyValueFactory<>("productVariety"));
        budgetColumn.setCellValueFactory(new PropertyValueFactory<>("budgetCompatibility"));
        reasonColumn.setCellValueFactory(new PropertyValueFactory<>("reason"));
    }

    private CustomerCartController getCartController() {
        return CartSession.getCartController();
    }

    private RecommendationRequestBean buildRecommendationRequestBean(
            String occasion,
            String style,
            String color,
            double budget
    ) {
        RecommendationRequestBean requestBean = new RecommendationRequestBean();
        requestBean.setOccasion(occasion);
        requestBean.setStyle(style);
        requestBean.setPreferredColor(color);
        requestBean.setMaxBudget(budget);
        return requestBean;
    }

    private Image loadProductImage(String imageName) {
        String resolvedImageName = (imageName == null || imageName.isBlank())
                ? DEFAULT_IMAGE
                : imageName;

        InputStream inputStream = getClass().getResourceAsStream(PRODUCT_IMAGES_PATH + resolvedImageName);

        if (inputStream == null) {
            inputStream = getClass().getResourceAsStream(PRODUCT_IMAGES_PATH + DEFAULT_IMAGE);
        }

        if (inputStream == null) {
            return null;
        }

        return new Image(inputStream);
    }
}