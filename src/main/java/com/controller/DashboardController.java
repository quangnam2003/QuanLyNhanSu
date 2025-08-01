package com.controller;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

public class DashboardController {

    @FXML
    public void openEmployees(MouseEvent event) {
        // Logic để mở trang nhân viên
        System.out.println("Chuyển đến trang Nhân viên");
    }

    @FXML
    public void openOrganization(MouseEvent event) {
        // Logic để mở trang tổ chức
        System.out.println("Chuyển đến trang Tổ chức");
    }

    @FXML
    public void openContracts(MouseEvent event) {
        // Logic để mở trang hợp đồng
        System.out.println("Chuyển đến trang Hợp đồng");
    }

    @FXML
    public void openDocuments(MouseEvent event) {
        // Logic để mở trang tài liệu
        System.out.println("Chuyển đến trang Tài liệu");
    }

    @FXML
    public void openSettings(MouseEvent event) {
        // Logic để mở trang cài đặt
        System.out.println("Chuyển đến trang Cài đặt");
    }

    @FXML
    public void openStatistics(MouseEvent event) {
        // Logic để mở trang thống kê
        System.out.println("Chuyển đến trang Thống kê");
    }
} 