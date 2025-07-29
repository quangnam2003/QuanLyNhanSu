 package com.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    private void handleLogin(ActionEvent event) {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        // Ví dụ kiểm tra tĩnh
        if ("admin".equals(user) && "123".equals(pass)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/main/main-layout.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(root, 1000, 600));
                stage.setTitle("Quản lý nhân sự");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            errorLabel.setText("Sai tên đăng nhập hoặc mật khẩu!");
        }
    }
}


