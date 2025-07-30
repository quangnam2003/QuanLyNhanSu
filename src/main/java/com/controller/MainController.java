package com.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class MainController {

    @FXML
    private StackPane contentArea;

    @FXML
    private VBox sidebar;

    @FXML
    private Button dashboardBtn;

    @FXML
    private Button employeeBtn;

    @FXML
    private Button organizationBtn;

    @FXML
    private Button contractBtn;

    @FXML
    private Button reportBtn;

    @FXML
    private Button settingsBtn;

    @FXML
    public void initialize() {
        // Load CSS
        contentArea.getStylesheets().add(getClass().getResource("/com/main/sidebar.css").toExternalForm());
        sidebar.getStylesheets().add(getClass().getResource("/com/main/sidebar.css").toExternalForm());

        // Load dashboard by default
        loadPage("dashboard");
    }

    @FXML
    public void handleMenu(javafx.event.ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String buttonText = clickedButton.getText();

        // Remove active class from all buttons
        removeActiveClass();

        // Add active class to clicked button
        clickedButton.getStyleClass().add("active");

        // Load corresponding page
        switch (buttonText) {
            case "Dashboard":
                loadPage("dashboard");
                break;
            case "Nhân viên":
                loadPage("employees");
                break;
            case "Tổ chức":
                loadPage("organization");
                break;
            case "Hợp đồng":
                loadPage("contracts");
                break;
            case "Báo cáo":
                loadPage("reports");
                break;
            case "Cài đặt":
                loadPage("settings");
                break;
            default:
                loadPage("dashboard");
        }
    }

    private void removeActiveClass() {
        dashboardBtn.getStyleClass().remove("active");
        employeeBtn.getStyleClass().remove("active");
        organizationBtn.getStyleClass().remove("active");
        contractBtn.getStyleClass().remove("active");
        reportBtn.getStyleClass().remove("active");
        settingsBtn.getStyleClass().remove("active");
    }

    private void loadPage(String page) {
        try {
            String fxmlPath = "/com/main/" + page + ".fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node pageContent = loader.load();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(pageContent);

        } catch (IOException e) {
            System.err.println("Could not load page: " + page);
            e.printStackTrace();

            // Fallback: show simple label
            contentArea.getChildren().clear();
            javafx.scene.control.Label errorLabel = new javafx.scene.control.Label("Trang " + page + " đang được phát triển");
            errorLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #7f8c8d; -fx-padding: 50;");
            contentArea.getChildren().add(errorLabel);
        }
    }
}
