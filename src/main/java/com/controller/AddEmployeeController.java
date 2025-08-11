package com.controller;

import com.model.Department;
import com.model.Employee;
import com.model.Position;
import com.service.DepartmentService;
import com.service.EmployeeService;
import com.service.PositionService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;

public class AddEmployeeController {

    @FXML private TextField txtFirstName;
    @FXML private TextField txtLastName;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPhone;
    @FXML private TextField txtCitizenId;
    @FXML private DatePicker dateBirth;
    @FXML private ComboBox<String> cbGender;
    @FXML private ComboBox<Department> cbDepartment;
    @FXML private ComboBox<Position> cbPosition;
    @FXML private DatePicker dateHire;
    @FXML private ComboBox<String> cbStatus;
    @FXML private TextField txtEmergencyName;
    @FXML private TextField txtEmergencyPhone;
    @FXML private TextField txtEmergencyRelation;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;

    private final EmployeeService employeeService = new EmployeeService();
    private final DepartmentService departmentService = new DepartmentService();
    private final PositionService positionService = new PositionService();
    private Employee editingEmployee = null;

    @FXML
    public void initialize() {
        cbGender.setItems(FXCollections.observableArrayList("Nam", "Nữ"));
        cbStatus.setItems(FXCollections.observableArrayList("Đang làm việc", "Đã nghỉ việc"));

        cbDepartment.setItems(FXCollections.observableArrayList(departmentService.getAllDepartments()));
        cbPosition.setItems(FXCollections.observableArrayList(positionService.getAllPositions()));

        //Kiểm tra nếu đang ở chế độ sửa
        if (editingEmployee != null) {
            cbDepartment.setValue(departmentService.getDepartmentById(editingEmployee.getDepartmentId()));
            cbPosition.setValue(positionService.getPositionById(editingEmployee.getPositionId()));
        }

        cbDepartment.setOnAction(event -> {
            Department selected = cbDepartment.getValue();
            if (selected != null) {
                List<Position> filtered = positionService.getPositionsByDepartmentId(selected.getId());
                cbPosition.getItems().setAll(filtered);
            } else {
                cbPosition.getItems().clear();
            }
        });


    }

    @FXML
    private void handleSave() {
        if (!isValidForm()) return;

        try {
            Employee emp = new Employee();
            emp.setFirstName(txtFirstName.getText().trim());
            emp.setLastName(txtLastName.getText().trim());
            emp.setEmail(txtEmail.getText().trim());
            emp.setPhone(txtPhone.getText().trim());
            emp.setCitizenId(txtCitizenId.getText().trim());
            emp.setDateOfBirth(dateBirth.getValue());
            emp.setGender(cbGender.getValue());
            emp.setHireDate(dateHire.getValue());
            emp.setEmploymentStatus(cbStatus.getValue());
            emp.setEmergencyContactName(txtEmergencyName.getText().trim());
            emp.setEmergencyContactPhone(txtEmergencyPhone.getText().trim());
            emp.setEmergencyContactRelationship(txtEmergencyRelation.getText().trim());
            emp.setDepartmentId(cbDepartment.getValue().getId());
            emp.setPositionId(cbPosition.getValue().getId());

            boolean success;
            if (editingEmployee == null) {
                success = employeeService.addEmployee(emp);
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã thêm nhân viên.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Thất bại", "Không thể thêm nhân viên.");
                }
            } else {
                emp.setId(editingEmployee.getId()); // giữ nguyên ID cũ
                success = employeeService.updateEmployee(emp);
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã cập nhật nhân viên.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Thất bại", "Không thể cập nhật nhân viên.");
                }
            }

            if (success) closeWindow();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi hệ thống", "Vui lòng kiểm tra lại dữ liệu.");
        }
    }



    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updatePositionList() {
        Department selectedDept = cbDepartment.getValue();
        if (selectedDept != null) {
            List<Position> filteredPositions = positionService.getPositionsByDepartmentId(selectedDept.getId());
            cbPosition.setItems(FXCollections.observableArrayList(filteredPositions));
            cbPosition.getSelectionModel().clearSelection();
        }
    }


    private boolean isValidForm() {
        StringBuilder msg = new StringBuilder();

        if (txtFirstName.getText().isBlank()) msg.append("Vui lòng nhập Họ.\n");
        if (txtLastName.getText().isBlank()) msg.append("Vui lòng nhập Tên.\n");
        if (txtEmail.getText().isBlank()) msg.append("Vui lòng nhập Email.\n");
        else if (employeeService.isEmailExists(txtEmail.getText().trim(),
                editingEmployee != null ? editingEmployee.getId() : null)) {
            msg.append("Email đã tồn tại trong hệ thống.\n");
        }
        else if (!txtEmail.getText().matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$")) msg.append("Email không hợp lệ.\n");

        if (txtPhone.getText().isBlank()) msg.append("Vui lòng nhập Số điện thoại.\n");
        else if (employeeService.isPhoneExists(txtPhone.getText().trim(),
                editingEmployee != null ? editingEmployee.getId() : null)) {
            msg.append("Số điện thoại đã tồn tại trong hệ thống.\n");
        }
        else if (!txtPhone.getText().matches("^\\d{8,15}$")) msg.append("Số điện thoại không hợp lệ.\n");

        if (txtCitizenId.getText().isBlank()) msg.append("Vui lòng nhập CCCD.\n");

        if (dateBirth.getValue() == null) msg.append("Vui lòng chọn Ngày sinh.\n");
        if (cbGender.getValue() == null) msg.append("Vui lòng chọn Giới tính.\n");

        if (cbDepartment.getValue() == null) msg.append("Vui lòng chọn Phòng ban.\n");
        if (cbPosition.getValue() == null) msg.append("Vui lòng chọn Chức vụ.\n");

        if (dateHire.getValue() == null) msg.append("Vui lòng chọn Ngày vào làm.\n");
        if (cbStatus.getValue() == null) msg.append("Vui lòng chọn Trạng thái làm việc.\n");

        if (!msg.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi nhập liệu", msg.toString());
            return false;
        }

        return true;
    }

    public void setEmployeeToEdit(Employee employee) {
        this.editingEmployee = employee;
        fillFormWithEmployeeData();
    }

    private void fillFormWithEmployeeData() {
        if (editingEmployee == null) return;

        txtFirstName.setText(editingEmployee.getFirstName());
        txtLastName.setText(editingEmployee.getLastName());
        txtEmail.setText(editingEmployee.getEmail());
        txtPhone.setText(editingEmployee.getPhone());
        txtCitizenId.setText(editingEmployee.getCitizenId());

        if (editingEmployee.getDateOfBirth() != null)
            dateBirth.setValue(editingEmployee.getDateOfBirth());

        cbGender.setValue(editingEmployee.getGender());

        cbDepartment.setValue(departmentService.getDepartmentById(editingEmployee.getDepartmentId()));
        cbPosition.setValue(positionService.getPositionById(editingEmployee.getPositionId()));

        if (editingEmployee.getHireDate() != null)
            dateHire.setValue(editingEmployee.getHireDate());

        cbStatus.setValue(editingEmployee.getEmploymentStatus());
        txtEmergencyName.setText(editingEmployee.getEmergencyContactName());
        txtEmergencyPhone.setText(editingEmployee.getEmergencyContactPhone());
        txtEmergencyRelation.setText(editingEmployee.getEmergencyContactRelationship());
    }
}