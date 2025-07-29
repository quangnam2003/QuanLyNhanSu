package com.controller;

import com.model.Department;
import com.model.Position;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import com.model.Employee;
import com.service.DepartmentService;
import com.service.EmployeeService;
import com.service.PositionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class EmployeeController implements Initializable {

    @FXML private TableView<Employee> employeeTable;

    @FXML private TableColumn<Employee, Integer> colId;
    @FXML private TableColumn<Employee, String> colFullName;
    @FXML private TableColumn<Employee, String> colEmail;
    @FXML private TableColumn<Employee, String> colPhone;
    @FXML private TableColumn<Employee, String> colGender;
    @FXML private TableColumn<Employee, LocalDate> colDateOfBirth;
    @FXML private TableColumn<Employee, String> colDepartment;
    @FXML private TableColumn<Employee, String> colPosition;
    @FXML private TableColumn<Employee, LocalDate> colHireDate;
    @FXML private TableColumn<Employee, String> colStatus;
    @FXML private TableColumn<Employee, Float> colSalaryGrade;

    @FXML
    private Button btnAddEmployee;

    @FXML
    private void onHoverAdd() {
        btnAddEmployee.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 5; -fx-cursor: hand;");
    }

    @FXML
    private void onExitAdd() {
        btnAddEmployee.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 5; -fx-cursor: hand;");
    }


    @FXML private ComboBox<String> departmentFilter;
    @FXML private ComboBox<String> positionFilter;
    @FXML
    private TableColumn<Employee, Void> actionColumn;


    @FXML
    private Label lblTotal;

    @FXML
    private Label lblWorking;

    @FXML
    private Label lblInactive;


    private final EmployeeService employeeService = new EmployeeService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadEmployeeData();
        loadDepartmentFilter(); // ⬅️ load danh sách phòng ban từ DB
        loadPositionFilter();
        departmentFilter.setOnAction(e -> filterEmployees());
        positionFilter.setOnAction(e -> filterEmployees());
        loadEmployeeStats();
        actionColumn.setCellFactory(new Callback<TableColumn<Employee, Void>, TableCell<Employee, Void>>() {
            @Override
            public TableCell<Employee, Void> call(final TableColumn<Employee, Void> param) {
                return new TableCell<>() {

                    private final Button btnEdit = new Button("Sửa");
                    private final Button btnDelete = new Button("Xoá");
                    private final HBox hBox = new HBox(btnEdit, btnDelete);

                    {
                        btnEdit.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5;");
                        btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 5;");

                        hBox.setSpacing(10);
                        hBox.setAlignment(Pos.CENTER);

                        btnEdit.setOnAction(event -> {
                            Employee employee = getTableView().getItems().get(getIndex());
                            handleEdit(employee);
                        });

                        btnDelete.setOnAction(event -> {
                            Employee employee = getTableView().getItems().get(getIndex());
                            handleDelete(employee);
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

    @FXML
    private void handleAddEmployee(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/view/add_employee.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Thêm nhân viên mới");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // chặn cửa sổ chính
            stage.showAndWait();

            // Reload lại table + tổng quan nếu có nhân viên mới
            loadEmployeeData();
            loadEmployeeStats();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleEdit(Employee employee) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/view/add_employee.fxml"));
            Parent root = loader.load();

            AddEmployeeController controller = loader.getController();
            controller.setEmployeeToEdit(employee);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Chỉnh sửa nhân viên");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadEmployeeData(); // reload sau khi đóng form
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void handleDelete(Employee employee) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xoá");
        alert.setHeaderText(null);
        alert.setContentText("Bạn có chắc chắn muốn xoá nhân viên " + employee.getFullName() + " không?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            employeeService.deleteEmployee(employee.getId());
            loadEmployeeData();
        }
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Dùng getFullName() thay vì field riêng
        colFullName.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFullName())
        );

        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        colDateOfBirth.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        colDepartment.setCellValueFactory(new PropertyValueFactory<>("departmentName"));
        colPosition.setCellValueFactory(new PropertyValueFactory<>("positionName"));
        colHireDate.setCellValueFactory(new PropertyValueFactory<>("hireDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("employmentStatus"));
        colSalaryGrade.setCellValueFactory(new PropertyValueFactory<>("salaryGrade"));
    }

    private void loadEmployeeData() {
        List<Employee> employeeList = employeeService.getAllEmployees();
        ObservableList<Employee> observableList = FXCollections.observableArrayList(employeeList);
        employeeTable.setItems(observableList);
    }

    private void loadDepartmentFilter() {
        DepartmentService departmentService = new DepartmentService();
        List<String> departments = departmentService.getAllDepartmentNames();
        departmentFilter.getItems().clear();
        departmentFilter.getItems().add("Tất cả");
        departmentFilter.getItems().addAll(departments);
    }

    private void loadPositionFilter() {
        PositionService positionService = new PositionService();
        List<String> positions = positionService.getAllPositionNames();
        positionFilter.getItems().clear();
        positionFilter.getItems().add("Tất cả");
        positionFilter.getItems().addAll(positions);
    }

    private void filterEmployees() {
        String selectedDepartment = departmentFilter.getValue();
        String selectedPosition = positionFilter.getValue();

        EmployeeService employeeService = new EmployeeService();
        List<Employee> filtered = employeeService.getFilteredEmployees(selectedDepartment, selectedPosition);
        employeeTable.setItems(FXCollections.observableArrayList(filtered));
    }

    private void loadEmployeeStats() {
        EmployeeService employeeService = new EmployeeService();
        Map<String, Integer> stats = employeeService.getEmployeeStats();

        lblTotal.setText(String.valueOf(stats.getOrDefault("total", 0)));
        lblWorking.setText(String.valueOf(stats.getOrDefault("working", 0)));
        lblInactive.setText(String.valueOf(stats.getOrDefault("inactive", 0)));
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
