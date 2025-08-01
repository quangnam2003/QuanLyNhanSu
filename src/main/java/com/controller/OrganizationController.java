package com.controller;

import com.model.Department;
import com.model.Employee;
import com.service.DepartmentService;
import com.service.EmployeeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.beans.property.SimpleStringProperty;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class OrganizationController implements Initializable {

    // Debug flag - set to false for production
    private static final boolean DEBUG = true;

    @FXML private TableView<Department> departmentTable;
    
    @FXML private TableColumn<Department, String> colDepartmentCode;
    @FXML private TableColumn<Department, String> colDepartmentName;
    @FXML private TableColumn<Department, String> colManagerName;
    @FXML private TableColumn<Department, Integer> colEmployeeCount;
    @FXML private TableColumn<Department, Void> actionColumn;
    
    @FXML private Button btnAddDepartment;

    @FXML private Label lblTotalDepartments;
    @FXML private Label lblDebugInfo;
    
    // Organizational Chart Elements
    @FXML private VBox organizationChart;
    @FXML private HBox departmentBoxes;
    
    private final DepartmentService departmentService = new DepartmentService();
    private final EmployeeService employeeService = new EmployeeService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        setupTableStyling();
        setupActionButtons();
        
        // Load dữ liệu với debug logging
        refreshAllData();
    }

    private void setupTableColumns() {
        // Cột mã phòng ban với styling
        colDepartmentCode.setCellValueFactory(new PropertyValueFactory<>("departmentCode"));
        colDepartmentCode.setCellFactory(column -> new TableCell<Department, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-font-weight: bold; -fx-text-fill: #3498db; -fx-alignment: CENTER; -fx-font-size: 13px;");
                }
            }
        });

        // Cột tên phòng ban
        colDepartmentName.setCellValueFactory(new PropertyValueFactory<>("departmentName"));
        colDepartmentName.setCellFactory(column -> new TableCell<Department, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-font-size: 14px;");
                }
            }
        });

        // Cột trưởng phòng với xử lý null
        colManagerName.setCellValueFactory(cellData -> {
            String managerName = cellData.getValue().getManagerName();
            return new SimpleStringProperty(managerName != null ? managerName : "Chưa có");
        });
        colManagerName.setCellFactory(column -> new TableCell<Department, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if ("Chưa có".equals(item)) {
                        setStyle("-fx-text-fill: #95a5a6; -fx-font-style: italic;");
                    } else {
                        setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    }
                }
            }
        });

        // Cột số nhân viên với badge styling
        colEmployeeCount.setCellValueFactory(new PropertyValueFactory<>("employeeCount"));
        colEmployeeCount.setCellFactory(column -> new TableCell<Department, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    Label badge = new Label(item + " nhân viên");
                    
                    // Màu sắc dựa trên số lượng nhân viên
                    String badgeStyle;
                    if (item == 0) {
                        badgeStyle = "-fx-background-color: #e74c3c; -fx-text-fill: white;";
                    } else if (item <= 5) {
                        badgeStyle = "-fx-background-color: #f39c12; -fx-text-fill: white;";
                    } else if (item <= 15) {
                        badgeStyle = "-fx-background-color: #27ae60; -fx-text-fill: white;";
                    } else {
                        badgeStyle = "-fx-background-color: #3498db; -fx-text-fill: white;";
                    }
                    
                    badge.setStyle(badgeStyle + " -fx-padding: 4 12; -fx-background-radius: 15; -fx-font-size: 12px; -fx-font-weight: bold;");
                    setText(null);
                    setGraphic(badge);
                    setAlignment(Pos.CENTER);
                }
            }
        });
    }

    private void setupTableStyling() {
        // Thêm CSS cho bảng
        departmentTable.setRowFactory(tv -> {
            TableRow<Department> row = new TableRow<>();
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem == null) {
                    row.setStyle("");
                } else {
                    // Alternating row colors
                    if (row.getIndex() % 2 == 0) {
                        row.setStyle("-fx-background-color: #ffffff;");
                    } else {
                        row.setStyle("-fx-background-color: #f8f9fa;");
                    }
                }
            });
            
            // Hover effect
            row.setOnMouseEntered(e -> {
                if (!row.isEmpty()) {
                    row.setStyle("-fx-background-color: #e3f2fd; -fx-cursor: hand;");
                }
            });
            
            row.setOnMouseExited(e -> {
                if (!row.isEmpty()) {
                    if (row.getIndex() % 2 == 0) {
                        row.setStyle("-fx-background-color: #ffffff;");
                    } else {
                        row.setStyle("-fx-background-color: #f8f9fa;");
                    }
                }
            });
            
            return row;
        });
    }

    private void setupActionButtons() {
        actionColumn.setCellFactory(new Callback<TableColumn<Department, Void>, TableCell<Department, Void>>() {
            @Override
            public TableCell<Department, Void> call(final TableColumn<Department, Void> param) {
                return new TableCell<>() {
                    private final Button btnEdit = new Button("✏️ Sửa");
                    private final Button btnViewEmployees = new Button("👥 Xem NV");
                    private final Button btnDelete = new Button("🗑️ Xoá");
                    private final HBox hBox = new HBox(btnEdit, btnViewEmployees, btnDelete);

                    {
                        // Styling cho button Sửa
                        btnEdit.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 4 8; -fx-font-size: 10px; -fx-font-weight: bold; -fx-cursor: hand; -fx-min-width: 45px;");
                        btnEdit.setOnMouseEntered(e -> btnEdit.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 4 8; -fx-font-size: 10px; -fx-font-weight: bold; -fx-cursor: hand; -fx-min-width: 45px;"));
                        btnEdit.setOnMouseExited(e -> btnEdit.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 4 8; -fx-font-size: 10px; -fx-font-weight: bold; -fx-cursor: hand; -fx-min-width: 45px;"));
                        
                        // Styling cho button Xem nhân viên
                        btnViewEmployees.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 4 8; -fx-font-size: 10px; -fx-font-weight: bold; -fx-cursor: hand; -fx-min-width: 65px;");
                        btnViewEmployees.setOnMouseEntered(e -> btnViewEmployees.setStyle("-fx-background-color: #229954; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 4 8; -fx-font-size: 10px; -fx-font-weight: bold; -fx-cursor: hand; -fx-min-width: 65px;"));
                        btnViewEmployees.setOnMouseExited(e -> btnViewEmployees.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 4 8; -fx-font-size: 10px; -fx-font-weight: bold; -fx-cursor: hand; -fx-min-width: 65px;"));
                        
                        // Styling cho button Xoá
                        btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 4 8; -fx-font-size: 10px; -fx-font-weight: bold; -fx-cursor: hand; -fx-min-width: 45px;");
                        btnDelete.setOnMouseEntered(e -> btnDelete.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 4 8; -fx-font-size: 10px; -fx-font-weight: bold; -fx-cursor: hand; -fx-min-width: 45px;"));
                        btnDelete.setOnMouseExited(e -> btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 4 8; -fx-font-size: 10px; -fx-font-weight: bold; -fx-cursor: hand; -fx-min-width: 45px;"));

                        hBox.setSpacing(5);
                        hBox.setAlignment(Pos.CENTER);

                        btnEdit.setOnAction(event -> {
                            Department department = getTableView().getItems().get(getIndex());
                            handleEditDepartment(department);
                        });

                        btnViewEmployees.setOnAction(event -> {
                            Department department = getTableView().getItems().get(getIndex());
                            handleViewEmployees(department);
                        });

                        btnDelete.setOnAction(event -> {
                            Department department = getTableView().getItems().get(getIndex());
                            handleDeleteDepartment(department);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(hBox);
                        }
                    }
                };
            }
        });
    }

    private void loadDepartmentData() {
        try {
            var departmentList = departmentService.getAllDepartmentsWithDetails();
            ObservableList<Department> observableList = FXCollections.observableArrayList(departmentList);
            departmentTable.setItems(observableList);
            
            // Cập nhật label tổng số
            updateTotalLabel(departmentList.size());
            
            // Tính tổng nhân viên và cập nhật debug info
            int totalEmployees = departmentList.stream().mapToInt(Department::getEmployeeCount).sum();
            updateDebugInfo(departmentList.size(), totalEmployees);
            
            // Debug: In ra thông tin để kiểm tra
            if (DEBUG) {
                System.out.println("=== DEBUG: Department Table Data ===");
                for (Department dept : departmentList) {
                    System.out.println(dept.getDepartmentName() + ": " + dept.getEmployeeCount() + " nhân viên");
                }
                System.out.println("Tổng cộng: " + totalEmployees + " nhân viên trong " + departmentList.size() + " phòng ban");
            }
        } catch (Exception e) {
            updateDebugInfo(0, 0);
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải dữ liệu phòng ban: " + e.getMessage());
        }
    }

    private void updateTotalLabel(int count) {
        if (lblTotalDepartments != null) {
            lblTotalDepartments.setText("📊 Tổng: " + count + " phòng ban");
        }
    }

    private void updateDebugInfo(int departmentCount, int totalEmployees) {
        if (lblDebugInfo != null) {
            lblDebugInfo.setText("👥 " + totalEmployees + " nhân viên trong " + departmentCount + " phòng ban");
        }
    }

    private void loadOrganizationChart() {
        try {
            Map<String, Integer> stats = departmentService.getDepartmentStats();
            
            // Debug: In ra thông tin để kiểm tra
            if (DEBUG) {
                System.out.println("=== DEBUG: Organization Chart Data ===");
                for (Map.Entry<String, Integer> entry : stats.entrySet()) {
                    System.out.println(entry.getKey() + ": " + entry.getValue() + " nhân viên");
                }
            }
            
            // Xóa các department boxes cũ
            departmentBoxes.getChildren().clear();
            
            // Tạo department boxes động dựa trên dữ liệu thực
            for (Map.Entry<String, Integer> entry : stats.entrySet()) {
                String deptName = entry.getKey();
                int employeeCount = entry.getValue();
                
                VBox deptBox = createDepartmentBox(deptName, employeeCount);
                departmentBoxes.getChildren().add(deptBox);
            }
            
        } catch (Exception e) {
            System.err.println("Lỗi khi tải sơ đồ tổ chức: " + e.getMessage());
        }
    }

    /**
     * Refresh toàn bộ dữ liệu để đảm bảo tính nhất quán
     */
    private void refreshAllData() {
        // Debug dữ liệu thô từ database
        if (DEBUG) {
            departmentService.debugEmployeeData();
        }
        
        loadDepartmentData();
        loadOrganizationChart();
        
        // Debug: Kiểm tra trạng thái nhân viên chi tiết
        if (DEBUG) {
            try {
                var detailedStats = departmentService.getDepartmentEmployeeStatusStats();
                System.out.println("=== DEBUG: Detailed Employee Status ===");
                for (Map.Entry<String, Map<String, Integer>> entry : detailedStats.entrySet()) {
                    System.out.println("Phòng " + entry.getKey() + ":");
                    for (Map.Entry<String, Integer> statusEntry : entry.getValue().entrySet()) {
                        System.out.println("  - " + statusEntry.getKey() + ": " + statusEntry.getValue() + " nhân viên");
                    }
                }
            } catch (Exception e) {
                System.err.println("Lỗi khi lấy thống kê chi tiết: " + e.getMessage());
            }
        }
    }

    private VBox createDepartmentBox(String departmentName, int employeeCount) {
        VBox deptBox = new VBox();
        deptBox.setAlignment(Pos.CENTER);
        deptBox.setSpacing(8);
        
        // Chọn màu dựa trên tên phòng ban
        String backgroundColor = getDepartmentColor(departmentName);
        deptBox.setStyle("-fx-background-color: linear-gradient(to bottom, " + backgroundColor + ", " + 
            getDarkerColor(backgroundColor) + "); -fx-padding: 18 25; -fx-background-radius: 12; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 3);");
        
        // Icon cho phòng ban
        String icon = getDepartmentIcon(departmentName);
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 24px;");
        
        Label nameLabel = new Label(departmentName);
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        Label countLabel = new Label(employeeCount + " nhân viên");
        countLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: rgba(255,255,255,0.9);");
        
        deptBox.getChildren().addAll(iconLabel, nameLabel, countLabel);
        
        // Hover effect
        deptBox.setOnMouseEntered(e -> {
            deptBox.setStyle("-fx-background-color: linear-gradient(to bottom, " + backgroundColor + ", " + 
                getDarkerColor(backgroundColor) + "); -fx-padding: 18 25; -fx-background-radius: 12; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 15, 0, 0, 5); -fx-cursor: hand; -fx-scale-x: 1.05; -fx-scale-y: 1.05;");
        });
        
        deptBox.setOnMouseExited(e -> {
            deptBox.setStyle("-fx-background-color: linear-gradient(to bottom, " + backgroundColor + ", " + 
                getDarkerColor(backgroundColor) + "); -fx-padding: 18 25; -fx-background-radius: 12; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 3);");
        });
        
        return deptBox;
    }

    private String getDepartmentIcon(String deptName) {
        if (deptName.contains("Nhân sự") || deptName.contains("HR")) {
            return "👥";
        } else if (deptName.contains("Tài chính") || deptName.contains("Kế toán")) {
            return "💰";
        } else if (deptName.contains("IT") || deptName.contains("Hạ tầng") || deptName.contains("Phát triển")) {
            return "💻";
        } else if (deptName.contains("Marketing")) {
            return "📈";
        } else if (deptName.contains("Quản lý") || deptName.contains("Sản phẩm")) {
            return "📦";
        } else if (deptName.contains("QA") || deptName.contains("Kiểm thử")) {
            return "🔍";
        } else {
            return "🏢";
        }
    }

    private String getDepartmentColor(String deptName) {
        // Màu sắc cho các phòng ban khác nhau
        if (deptName.contains("Nhân sự") || deptName.contains("HR")) {
            return "#27ae60";
        } else if (deptName.contains("Tài chính") || deptName.contains("Kế toán")) {
            return "#e74c3c";
        } else if (deptName.contains("IT") || deptName.contains("Hạ tầng") || deptName.contains("Phát triển")) {
            return "#f39c12";
        } else if (deptName.contains("Marketing")) {
            return "#9b59b6";
        } else if (deptName.contains("Quản lý") || deptName.contains("Sản phẩm")) {
            return "#3498db";
        } else if (deptName.contains("QA") || deptName.contains("Kiểm thử")) {
            return "#1abc9c";
        } else {
            return "#95a5a6"; // Màu mặc định
        }
    }

    private String getDarkerColor(String color) {
        // Tạo màu tối hơn cho gradient
        switch (color) {
            case "#27ae60": return "#229954";
            case "#e74c3c": return "#c0392b";
            case "#f39c12": return "#d68910";
            case "#9b59b6": return "#8e44ad";
            case "#3498db": return "#2980b9";
            case "#1abc9c": return "#17a2b8";
            default: return "#7f8c8d";
        }
    }



    @FXML
    private void handleAddDepartment(ActionEvent event) {
        try {
            // Tạo dialog để thêm phòng ban mới
            Dialog<Department> dialog = createDepartmentDialog(null);
            Optional<Department> result = dialog.showAndWait();
            
            if (result.isPresent()) {
                Department newDept = result.get();
                
                // Kiểm tra mã phòng ban đã tồn tại chưa
                if (departmentService.isDepartmentCodeExists(newDept.getDepartmentCode(), null)) {
                    showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Mã phòng ban đã tồn tại!");
                    return;
                }
                
                boolean success = departmentService.addDepartment(newDept);
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Thành công", "Thêm phòng ban thành công!");
                    refreshAllData(); // Sử dụng refreshAllData() thay vì gọi riêng lẻ
                } else {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể thêm phòng ban!");
                }
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Đã xảy ra lỗi: " + e.getMessage());
        }
    }

    private void handleEditDepartment(Department department) {
        try {
            Dialog<Department> dialog = createDepartmentDialog(department);
            Optional<Department> result = dialog.showAndWait();
            
            if (result.isPresent()) {
                Department updatedDept = result.get();
                
                // Kiểm tra mã phòng ban (trừ chính nó)
                if (departmentService.isDepartmentCodeExists(updatedDept.getDepartmentCode(), department.getId())) {
                    showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Mã phòng ban đã tồn tại!");
                    return;
                }
                
                boolean success = departmentService.updateDepartment(updatedDept);
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Thành công", "Cập nhật phòng ban thành công!");
                    refreshAllData(); // Sử dụng refreshAllData() thay vì gọi riêng lẻ
                } else {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể cập nhật phòng ban!");
                }
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Đã xảy ra lỗi: " + e.getMessage());
        }
    }

    private void handleViewEmployees(Department department) {
        try {
            // Lấy danh sách nhân viên của phòng ban
            List<Employee> employees = employeeService.searchEmployees(null, department.getId(), null);
            
            // Tạo dialog hiển thị danh sách nhân viên
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("👥 Danh sách nhân viên - " + department.getDepartmentName());
            dialog.setHeaderText(null);
            dialog.setResizable(true);
            
            // Tạo TableView cho nhân viên
            TableView<Employee> employeeTable = new TableView<>();
            employeeTable.setStyle("-fx-font-size: 13px;");
            
            // Cột Tên
            TableColumn<Employee, String> nameCol = new TableColumn<>("👤 Họ tên");
            nameCol.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getFirstName() + " " + data.getValue().getLastName()));
            nameCol.setPrefWidth(180);
            
            // Cột Email  
            TableColumn<Employee, String> emailCol = new TableColumn<>("📧 Email");
            emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
            emailCol.setPrefWidth(200);
            
            // Cột Điện thoại
            TableColumn<Employee, String> phoneCol = new TableColumn<>("📞 Điện thoại");
            phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
            phoneCol.setPrefWidth(130);
            
            // Cột Trạng thái
            TableColumn<Employee, String> statusCol = new TableColumn<>("📊 Trạng thái");
            statusCol.setCellValueFactory(new PropertyValueFactory<>("employmentStatus"));
            statusCol.setPrefWidth(120);
            statusCol.setCellFactory(column -> new TableCell<Employee, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        Label statusLabel = new Label(item);
                        if ("Active".equals(item)) {
                            statusLabel.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 4 8; -fx-background-radius: 12; -fx-font-size: 11px;");
                        } else {
                            statusLabel.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 4 8; -fx-background-radius: 12; -fx-font-size: 11px;");
                        }
                        setText(null);
                        setGraphic(statusLabel);
                        setAlignment(Pos.CENTER);
                    }
                }
            });
            
            employeeTable.getColumns().addAll(nameCol, emailCol, phoneCol, statusCol);
            employeeTable.setItems(FXCollections.observableArrayList(employees));
            
            // Layout
            VBox content = new VBox(15);
            content.setStyle("-fx-padding: 20;");
            
            Label infoLabel = new Label("📋 Tổng số: " + employees.size() + " nhân viên");
            infoLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
            
            content.getChildren().addAll(infoLabel, employeeTable);
            
            // Set size
            employeeTable.setPrefHeight(400);
            employeeTable.setPrefWidth(650);
            
            dialog.getDialogPane().setContent(content);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            
            // Styling cho button
            dialog.getDialogPane().lookupButton(ButtonType.OK).setStyle(
                "-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 5;"
            );
            
            dialog.showAndWait();
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải danh sách nhân viên: " + e.getMessage());
        }
    }

    private void handleDeleteDepartment(Department department) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận xoá");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Bạn có chắc chắn muốn xoá phòng ban \"" + department.getDepartmentName() + "\" không?\n" +
                "Lưu ý: Không thể xoá phòng ban đang có nhân viên.");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = departmentService.deleteDepartment(department.getId());
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Xoá phòng ban thành công!");
                refreshAllData(); // Sử dụng refreshAllData() thay vì gọi riêng lẻ
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", 
                    "Không thể xoá phòng ban!\nPhòng ban này có thể đang có nhân viên hoặc đã xảy ra lỗi.");
            }
        }
    }

    private Dialog<Department> createDepartmentDialog(Department existingDept) {
        Dialog<Department> dialog = new Dialog<>();
        dialog.setTitle(existingDept == null ? "➕ Thêm phòng ban mới" : "✏️ Chỉnh sửa phòng ban");
        dialog.setHeaderText(null);

        // Tạo form fields với styling
        TextField codeField = new TextField();
        TextField nameField = new TextField();
        TextField descField = new TextField();
        TextField addressField = new TextField();
        TextField phoneField = new TextField();
        TextField emailField = new TextField();

        // Styling cho text fields
        String fieldStyle = "-fx-padding: 10; -fx-border-color: #bdc3c7; -fx-border-radius: 5; -fx-background-radius: 5; -fx-min-width: 250px;";
        codeField.setStyle(fieldStyle);
        nameField.setStyle(fieldStyle);
        descField.setStyle(fieldStyle);
        addressField.setStyle(fieldStyle);
        phoneField.setStyle(fieldStyle);
        emailField.setStyle(fieldStyle);

        // Điền dữ liệu nếu đang edit
        if (existingDept != null) {
            codeField.setText(existingDept.getDepartmentCode());
            nameField.setText(existingDept.getDepartmentName());
            descField.setText(existingDept.getDescription());
            addressField.setText(existingDept.getAddress());
            phoneField.setText(existingDept.getPhone());
            emailField.setText(existingDept.getEmail());
        }

        // Layout với GridPane để label và field cùng hàng
        GridPane form = new GridPane();
        form.setStyle("-fx-padding: 20;");
        form.setHgap(15); // Khoảng cách ngang giữa label và field
        form.setVgap(15); // Khoảng cách dọc giữa các rows
        
        String labelStyle = "-fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-min-width: 120px;";
        
        Label codeLabel = new Label("🏷️ Mã phòng ban:");
        codeLabel.setStyle(labelStyle);
        Label nameLabel = new Label("🏢 Tên phòng ban:");
        nameLabel.setStyle(labelStyle);
        Label descLabel = new Label("📝 Mô tả:");
        descLabel.setStyle(labelStyle);
        Label addressLabel = new Label("🏠 Địa chỉ:");
        addressLabel.setStyle(labelStyle);
        Label phoneLabel = new Label("📞 Điện thoại:");
        phoneLabel.setStyle(labelStyle);
        Label emailLabel = new Label("📧 Email:");
        emailLabel.setStyle(labelStyle);
        
        // Thêm các components vào GridPane theo format (column, row)
        form.add(codeLabel, 0, 0);
        form.add(codeField, 1, 0);
        form.add(nameLabel, 0, 1);
        form.add(nameField, 1, 1);
        form.add(descLabel, 0, 2);
        form.add(descField, 1, 2);
        form.add(addressLabel, 0, 3);
        form.add(addressField, 1, 3);
        form.add(phoneLabel, 0, 4);
        form.add(phoneField, 1, 4);
        form.add(emailLabel, 0, 5);
        form.add(emailField, 1, 5);

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        // Styling cho dialog buttons
        dialog.getDialogPane().lookupButton(ButtonType.OK).setStyle(
            "-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 5;"
        );
        dialog.getDialogPane().lookupButton(ButtonType.CANCEL).setStyle(
            "-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 5;"
        );

        // Convert result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                Department dept = existingDept != null ? existingDept : new Department();
                dept.setDepartmentCode(codeField.getText().trim());
                dept.setDepartmentName(nameField.getText().trim());
                dept.setDescription(descField.getText().trim());
                dept.setAddress(addressField.getText().trim());
                dept.setPhone(phoneField.getText().trim());
                dept.setEmail(emailField.getText().trim());
                return dept;
            }
            return null;
        });

        return dialog;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Button hover effects
    @FXML
    private void onHoverAdd() {
        btnAddDepartment.setStyle("-fx-background-color: #229954; -fx-text-fill: white; -fx-padding: 12 24; -fx-background-radius: 25; -fx-font-size: 14px; -fx-font-weight: bold; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 12, 0, 0, 3); -fx-cursor: hand; -fx-scale-x: 1.05; -fx-scale-y: 1.05;");
    }

    @FXML
    private void onExitAdd() {
        btnAddDepartment.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 12 24; -fx-background-radius: 25; -fx-font-size: 14px; -fx-font-weight: bold; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 8, 0, 0, 2); -fx-cursor: hand;");
    }
} 