package com.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class Homepage extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Load giao diện login.fxml thay vì main-layout.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/main/login.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 400, 300); // chỉnh kích thước login
        stage.setTitle("Đăng nhập");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

