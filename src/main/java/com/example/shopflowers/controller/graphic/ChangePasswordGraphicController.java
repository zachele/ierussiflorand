package com.example.shopflowers.controller.graphic;

import com.example.shopflowers.controller.application.ChangePasswordController;
import com.example.shopflowers.model.bean.ChangePasswordBean;
import com.example.shopflowers.util.AlertUtils;
import com.example.shopflowers.util.SceneNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class ChangePasswordGraphicController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField oldPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label messageLabel;

    private final ChangePasswordController changePasswordController = new ChangePasswordController();

    @FXML
    private void handleChangePassword() {
        ChangePasswordBean changePasswordBean = buildChangePasswordBean();

        try {
            boolean changed = changePasswordController.changePassword(changePasswordBean);

            if (!changed) {
                messageLabel.setText("Dati non validi, password attuale errata o conferma non corretta.");
                return;
            }

            AlertUtils.showInfo(
                    "Password",
                    "Password aggiornata con successo."
            );
        } catch (SQLException e) {
            AlertUtils.showWarning(
                    "Cambio password",
                    "La password attuale non è corretta."
            );
        }
    }

    @FXML
    private void handleBackToLogin() {
        try {
            SceneNavigator.goTo(
                    (Stage) messageLabel.getScene().getWindow(),
                    "/com/example/shopflowers/login-view.fxml",
                    "Shop Flowers - Login"
            );
        } catch (IOException e) {
            messageLabel.setText("Errore nel ritorno alla login.");
        }
    }

    private ChangePasswordBean buildChangePasswordBean() {
        ChangePasswordBean changePasswordBean = new ChangePasswordBean();
        changePasswordBean.setUsername(usernameField.getText());
        changePasswordBean.setOldPassword(oldPasswordField.getText());
        changePasswordBean.setNewPassword(newPasswordField.getText());
        changePasswordBean.setConfirmPassword(confirmPasswordField.getText());
        return changePasswordBean;
    }
}