/* 
package com.main;

import com.utils.PermissionManager;
import com.utils.SessionManager;
import com.model.User;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class TestAdminPermissions extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        // Tạo user admin
        User adminUser = new User(1, "admin@techcorp.com", "admin@techcorp.com", 1, "Admin", "ADMIN");
        SessionManager.getInstance().setCurrentUser(adminUser);
        
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        
        // Tiêu đề
        Label titleLabel = new Label("Test Quyền Admin - admin@techcorp.com");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Tạo các button test
        Button employeeBtn = new Button("Nhân viên");
        Button organizationBtn = new Button("Tổ chức");
        Button contractBtn = new Button("Hợp đồng");
        Button reportBtn = new Button("Hướng dẫn");
        Button settingsBtn = new Button("Cài đặt");
        
        // Thiết lập tooltip cho từng button
        PermissionManager permissionManager = PermissionManager.getInstance();
        
        // Kiểm tra và hiển thị quyền
        VBox permissionInfo = new VBox(10);
        permissionInfo.setAlignment(Pos.CENTER_LEFT);
        permissionInfo.setPadding(new Insets(20));
        permissionInfo.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8; -fx-padding: 15;");
        
        Label infoTitle = new Label("Thông tin quyền:");
        infoTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        // Kiểm tra từng quyền
        checkAndDisplayPermission(permissionInfo, "VIEW_EMPLOYEE", "Xem nhân viên");
        checkAndDisplayPermission(permissionInfo, "VIEW_DEPARTMENT", "Xem tổ chức");
        checkAndDisplayPermission(permissionInfo, "VIEW_CONTRACT", "Xem hợp đồng");
        checkAndDisplayPermission(permissionInfo, "VIEW_REPORT", "Xem hướng dẫn");
        checkAndDisplayPermission(permissionInfo, "MANAGE_ROLE", "Quản lý vai trò");
        
        permissionInfo.getChildren().add(0, infoTitle);
        
        // Thêm style cho button
        employeeBtn.setStyle("-fx-font-size: 16px; -fx-padding: 10 20; -fx-background-color: #3498db; -fx-text-fill: white;");
        organizationBtn.setStyle("-fx-font-size: 16px; -fx-padding: 10 20; -fx-background-color: #3498db; -fx-text-fill: white;");
        contractBtn.setStyle("-fx-font-size: 16px; -fx-padding: 10 20; -fx-background-color: #3498db; -fx-text-fill: white;");
        reportBtn.setStyle("-fx-font-size: 16px; -fx-padding: 10 20; -fx-background-color: #3498db; -fx-text-fill: white;");
        settingsBtn.setStyle("-fx-font-size: 16px; -fx-padding: 10 20; -fx-background-color: #3498db; -fx-text-fill: white;");
        
        root.getChildren().addAll(titleLabel, employeeBtn, organizationBtn, contractBtn, reportBtn, settingsBtn, permissionInfo);
        
        Scene scene = new Scene(root, 500, 600);
        primaryStage.setTitle("Test Admin Permissions");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void checkAndDisplayPermission(VBox container, String permissionCode, String permissionName) {
        PermissionManager permissionManager = PermissionManager.getInstance();
        boolean hasPermission = permissionManager.hasPermission(permissionCode);
        
        Label label = new Label();
        if (hasPermission) {
            label.setText("✅ " + permissionName + " - CÓ QUYỀN");
            label.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        } else {
            label.setText("❌ " + permissionName + " - KHÔNG CÓ QUYỀN");
            label.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        }
        
        container.getChildren().add(label);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
} */