package com.example.shopflowers.controller.graphic;

import com.example.shopflowers.ShopFlowersApplication;
import com.example.shopflowers.config.AppConfig;
import com.example.shopflowers.config.AppMode;
import com.example.shopflowers.controller.application.CustomerOrdersController;
import com.example.shopflowers.controller.application.LoginController;
import com.example.shopflowers.exception.InvalidCredentialsException;
import com.example.shopflowers.model.bean.LoginBean;
import com.example.shopflowers.model.entity.OrderSummary;
import com.example.shopflowers.model.entity.User;
import com.example.shopflowers.util.SceneNavigator;
import com.example.shopflowers.util.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class LoginGraphicController {

    private static final Logger LOGGER = Logger.getLogger(LoginGraphicController.class.getName());

    @FXML
    private MenuButton modeMenuButton;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    @FXML
    private TextField visiblePasswordField;

    private LoginController loginController;

    @FXML
    public void initialize() {
        updateModeLabel();
    }

    @FXML
    private void handleSetDemoMode() {
        applyMode(AppMode.DEMO);
    }

    @FXML
    private void handleSetFileMode() {
        applyMode(AppMode.FILE);
    }

    @FXML
    private void handleSetFullMode() {
        applyMode(AppMode.FULL);
    }

    private void applyMode(AppMode mode) {
        AppConfig.setMode(mode);
        Session.getInstance().clearSession();
        updateModeLabel();
        messageLabel.setText("Modalità applicata: " + mode.name());
    }

    private void updateModeLabel() {
        if (modeMenuButton != null) {
            modeMenuButton.setText("⚙");
        }
    }

    @FXML
    private void handleLogin() {
        LoginBean loginBean = buildLoginBean();

        try {
            loginController = new LoginController();
            User user = loginController.login(loginBean);

            Session.getInstance().setSession(user.getUsername(), user.getRole());

            switch (user.getRole()) {
                case "ADMIN" -> openView("/com/example/shopflowers/admin-product-view.fxml", "Shop Flowers - Admin");
                case "CUSTOMER" -> {
                    openView("/com/example/shopflowers/catalog-view.fxml", "Shop Flowers - Catalogo");
                    checkOrderStatusNotifications(user.getUsername());
                }
                case "OPERATOR" -> openView("/com/example/shopflowers/operator-view.fxml", "Shop Flowers - Operatore");
                default -> messageLabel.setText("Accesso non riuscito. Ruolo utente non riconosciuto.");
            }

        } catch (InvalidCredentialsException e) {
            messageLabel.setText(e.getMessage());
        } catch (SQLException e) {
            messageLabel.setText("Si è verificato un errore durante il login.");
        } catch (IOException e) {
            messageLabel.setText("Si è verificato un errore durante l'apertura della schermata.");
        }
    }

    @FXML
    private void handleGuestAccess() {
        try {
            Session.getInstance().clearSession();
            Session.getInstance().setSession("guest", "GUEST");
            openView("/com/example/shopflowers/catalog-view.fxml", "Shop Flowers - Catalogo Ospite");
        } catch (IOException e) {
            messageLabel.setText("Si è verificato un errore durante l'accesso come ospite.");
        }
    }

    private LoginBean buildLoginBean() {
        LoginBean loginBean = new LoginBean();
        loginBean.setUsername(usernameField.getText());
        loginBean.setPassword(getCurrentPassword());
        return loginBean;
    }

    private String getCurrentPassword() {
        return passwordField.isVisible()
                ? passwordField.getText()
                : visiblePasswordField.getText();
    }

    private void checkOrderStatusNotifications(String username) {
        try {
            CustomerOrdersController customerOrdersController = new CustomerOrdersController();
            List<OrderSummary> updatedOrders = customerOrdersController.getOrdersWithStatusUpdate(username);

            if (updatedOrders.isEmpty()) {
                return;
            }

            StringBuilder message = new StringBuilder("Stato ordini aggiornato:\n\n");

            for (OrderSummary order : updatedOrders) {
                message.append("Ordine n. ")
                        .append(order.getId())
                        .append(" → ")
                        .append(order.getStatus())
                        .append("\n");
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Aggiornamento ordini");
            alert.setHeaderText("Hai aggiornamenti sui tuoi ordini");
            alert.setContentText(message.toString());
            alert.showAndWait();

            customerOrdersController.markOrdersAsNotified(username);

        } catch (SQLException e) {
            LOGGER.severe("Errore nel caricamento notifiche ordini: " + e.getMessage());
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
            SceneNavigator.goTo(
                    (Stage) usernameField.getScene().getWindow(),
                    "/com/example/shopflowers/register-view.fxml",
                    "Shop Flowers - Registrazione"
            );
        } catch (IOException e) {
            messageLabel.setText("Si è verificato un errore durante l'apertura della schermata di registrazione.");
        }
    }

    @FXML
    private void handleGoToChangePassword() {
        try {
            SceneNavigator.goTo(
                    (Stage) usernameField.getScene().getWindow(),
                    "/com/example/shopflowers/change-password-view.fxml",
                    "Shop Flowers - Cambio Password"
            );
        } catch (IOException e) {
            messageLabel.setText("Si è verificato un errore durante l'apertura della schermata di cambio password.");
        }
    }

    @FXML
    private void handleTogglePassword() {
        if (passwordField.isVisible()) {
            visiblePasswordField.setText(passwordField.getText());
            visiblePasswordField.setVisible(true);
            visiblePasswordField.setManaged(true);

            passwordField.setVisible(false);
            passwordField.setManaged(false);
        } else {
            passwordField.setText(visiblePasswordField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);

            visiblePasswordField.setVisible(false);
            visiblePasswordField.setManaged(false);
        }
    }
}