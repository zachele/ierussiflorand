package com.example.shopflowers.controller.graphic;

import com.example.shopflowers.config.UiTitles;
import com.example.shopflowers.config.ViewPaths;
import com.example.shopflowers.controller.application.CustomerCartController;
import com.example.shopflowers.controller.application.RecommendationController;
import com.example.shopflowers.exception.InvalidQuantityException;
import com.example.shopflowers.model.bean.RecommendationRequestBean;
import com.example.shopflowers.model.entity.RecommendationResult;
import com.example.shopflowers.util.CartSession;
import com.example.shopflowers.util.ProductTableImageCellFactory;
import com.example.shopflowers.util.SceneNavigator;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class RecommendationGraphicController {

    private static final String LOAD_RECOMMENDATIONS_ERROR_MESSAGE =
            "Errore nel caricamento delle proposte.";
    private static final String BACK_TO_CATALOG_ERROR_MESSAGE =
            "Si è verificato un errore durante il ritorno al catalogo.";
    private static final String LOGOUT_ERROR_MESSAGE =
            "Si è verificato un errore durante il logout.";

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
        configureComboBoxes();
        configureRecommendationTable();
        configureSelectionListener();
    }

    @FXML
    private void handleFindRecommendations() {
        if (!areRecommendationInputsValid()) {
            return;
        }

        try {
            RecommendationRequestBean requestBean = buildRecommendationRequestBean(
                    occasionComboBox.getValue(),
                    styleComboBox.getValue(),
                    colorComboBox.getValue(),
                    Double.parseDouble(budgetField.getText())
            );

            List<RecommendationResult> results = recommendationController.getRecommendations(requestBean);
            recommendationTable.setItems(FXCollections.observableArrayList(results));
            updateRecommendationMessage(results);

        } catch (NumberFormatException e) {
            messageLabel.setText("Inserisci un budget valido.");
        } catch (SQLException e) {
            messageLabel.setText(LOAD_RECOMMENDATIONS_ERROR_MESSAGE);
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
                    ViewPaths.CATALOG_VIEW,
                    UiTitles.CATALOG_CUSTOMER
            );
        } catch (IOException e) {
            messageLabel.setText(BACK_TO_CATALOG_ERROR_MESSAGE);
        }
    }

    @FXML
    @SuppressWarnings("unused")
    private void handleLogout() {
        try {
            SceneNavigator.logoutToLogin((Stage) messageLabel.getScene().getWindow());
        } catch (IOException e) {
            messageLabel.setText(LOGOUT_ERROR_MESSAGE);
        }
    }

    private void configureComboBoxes() {
        occasionComboBox.setItems(FXCollections.observableArrayList(
                "COMPLEANNO",
                "ANNIVERSARIO",
                "LAUREA",
                "RINGRAZIAMENTO",
                "CONDOGLIANZE",
                "ROMANTICO"
        ));

        styleComboBox.setItems(FXCollections.observableArrayList(
                "ROMANTICO",
                "ELEGANTE",
                "ALLEGRO",
                "SEMPLICE",
                "RAFFINATO"
        ));

        colorComboBox.setItems(FXCollections.observableArrayList(
                "ROSSO",
                "BIANCO",
                "ROSA",
                "GIALLO",
                "MISTO",
                "NESSUNA"
        ));
    }

    private void configureRecommendationTable() {
        imageColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue() != null && cellData.getValue().getProduct() != null
                                ? cellData.getValue().getProduct().getImageName()
                                : null
                )
        );
        imageColumn.setCellFactory(ProductTableImageCellFactory.create());

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("productPrice"));
        colorColumn.setCellValueFactory(new PropertyValueFactory<>("productColor"));
        varietyColumn.setCellValueFactory(new PropertyValueFactory<>("productVariety"));
        budgetColumn.setCellValueFactory(new PropertyValueFactory<>("budgetCompatibility"));
        reasonColumn.setCellValueFactory(new PropertyValueFactory<>("reason"));
    }

    private void configureSelectionListener() {
        recommendationTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) ->
                selectedRecommendation = newValue);
    }

    private boolean areRecommendationInputsValid() {
        String occasion = occasionComboBox.getValue();
        String style = styleComboBox.getValue();
        String color = colorComboBox.getValue();
        String budgetText = budgetField.getText();

        if (occasion == null || style == null || color == null || budgetText == null || budgetText.isBlank()) {
            messageLabel.setText("Compila tutti i campi dell'assistente.");
            return false;
        }

        try {
            Double.parseDouble(budgetText);
            return true;
        } catch (NumberFormatException e) {
            messageLabel.setText("Inserisci un budget valido.");
            return false;
        }
    }

    private void updateRecommendationMessage(List<RecommendationResult> results) {
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
}