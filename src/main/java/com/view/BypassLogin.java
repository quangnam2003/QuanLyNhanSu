package com.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class BypassLogin extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Load trực tiếp giao diện main-layout.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/main/main-layout.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 1000, 600);
        stage.setScene(scene);
        stage.setTitle("Quản lý nhân sự");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}