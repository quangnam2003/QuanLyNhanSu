/* package com.main;

import com.utils.PermissionTooltipManager;
import com.utils.SessionManager;
import com.model.User;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class TestPermissionTooltip extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        // Tạo user test với role HR
        User testUser = new User(1, "hr_user", "password", 4, "HR Staff", "HR");
        SessionManager.getInstance().setCurrentUser(testUser);
        
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        
        // Tạo các button test
        Button employeeBtn = new Button("Nhân viên");
        Button organizationBtn = new Button("Tổ chức");
        Button contractBtn = new Button("Hợp đồng");
        Button reportBtn = new Button("Báo cáo");
        Button settingsBtn = new Button("Cài đặt");
        
        // Thiết lập tooltip cho từng button
        PermissionTooltipManager tooltipManager = PermissionTooltipManager.getInstance();
        tooltipManager.setupPermissionTooltip(employeeBtn, "VIEW_EMPLOYEE", "Nhân viên");
        tooltipManager.setupPermissionTooltip(organizationBtn, "VIEW_DEPARTMENT", "Tổ chức");
        tooltipManager.setupPermissionTooltip(contractBtn, "VIEW_CONTRACT", "Hợp đồng");
        tooltipManager.setupPermissionTooltip(reportBtn, "VIEW_REPORT", "Báo cáo");
        tooltipManager.setupPermissionTooltip(settingsBtn, "MANAGE_ROLE", "Cài đặt");
        
        // Thêm style cho button
        employeeBtn.setStyle("-fx-font-size: 16px; -fx-padding: 10 20;");
        organizationBtn.setStyle("-fx-font-size: 16px; -fx-padding: 10 20;");
        contractBtn.setStyle("-fx-font-size: 16px; -fx-padding: 10 20;");
        reportBtn.setStyle("-fx-font-size: 16px; -fx-padding: 10 20;");
        settingsBtn.setStyle("-fx-font-size: 16px; -fx-padding: 10 20;");
        
        root.getChildren().addAll(employeeBtn, organizationBtn, contractBtn, reportBtn, settingsBtn);
        
        Scene scene = new Scene(root, 400, 500);
        primaryStage.setTitle("Test Permission Tooltip - HR User");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
} */