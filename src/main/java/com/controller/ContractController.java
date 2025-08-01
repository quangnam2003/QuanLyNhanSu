package com.controller;

import com.model.Contract;
import com.model.Employee;
import com.service.ContractService;
import com.service.EmployeeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.paint.Color;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ContractController implements Initializable {

    // Statistics Labels
    @FXML private Label activeContractsLabel;
    @FXML private Label expiringContractsLabel;
    @FXML private Label expiredContractsLabel;

    // Contract Type Statistics
    @FXML private Label longTermContractsLabel;
    @FXML private Label shortTermContractsLabel;
    @FXML private Label probationContractsLabel;

    // Search and Filter Controls
    @FXML private TextField searchField;
    @FXML private ComboBox<String> contractTypeComboBox;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private DatePicker fromDatePicker;
    @FXML private Button createContractButton;

    // Table and Columns
    @FXML private TableView<Contract> contractTable;
    @FXML private TableColumn<Contract, Integer> indexColumn;
    @FXML private TableColumn<Contract, String> contractCodeColumn;
    @FXML private TableColumn<Contract, String> employeeNameColumn;
    @FXML private TableColumn<Contract, String> contractTypeColumn;
    @FXML private TableColumn<Contract, LocalDate> startDateColumn;
    @FXML private TableColumn<Contract, LocalDate> endDateColumn;
    @FXML private TableColumn<Contract, BigDecimal> salaryColumn;
    @FXML private TableColumn<Contract, String> statusColumn;
    @FXML private TableColumn<Contract, Void> actionColumn;

    private ContractService contractService;
    private EmployeeService employeeService;
    private ObservableList<Contract> contractList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        contractService = new ContractService();
        employeeService = new EmployeeService();
        contractList = FXCollections.observableArrayList();

        initializeTable();
        initializeComboBoxes();
        loadContractData();
        loadStatistics();
        setupSearchAndFilter();
    }

    private void initializeTable() {
        indexColumn.setCellFactory(col -> new TableCell<Contract, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 1));
                }
            }
        });
        indexColumn.setSortable(false);
        // Set up table columns
        contractCodeColumn.setCellValueFactory(new PropertyValueFactory<>("contractNumber"));
        employeeNameColumn.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        contractTypeColumn.setCellValueFactory(new PropertyValueFactory<>("contractTypeName"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        // Custom cell factory for salary column to format currency
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("salary"));
        salaryColumn.setCellFactory(column -> new TableCell<Contract, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.0f ₫", item));
                }
            }
        });

        salaryColumn.setCellFactory(column -> new TableCell<Contract, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.0f ₫", item)); // Hiển thị số có dấu chấm và VND
                }
            }
        });


        // Custom cell factory for status column with colored indicators
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusColumn.setCellFactory(column -> new TableCell<Contract, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    switch (status.toLowerCase()) {
                        case "active":
                            setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                            break;
                        case "expired":
                            setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                            break;
                        case "renewed":
                            setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;");
                            break;
                        case "terminated":
                            setStyle("-fx-text-fill: #95a5a6; -fx-font-weight: bold;");
                            break;
                        default:
                            setStyle("-fx-text-fill: #2c3e50;");
                    }
                }
            }
        });

        // Set up action column with buttons
        actionColumn.setCellFactory(column -> new TableCell<Contract, Void>() {
            private final Button viewButton = new Button("Xem");
            private final Button editButton = new Button("Sửa");
            private final Button deleteButton = new Button("Xóa");
            private final HBox buttonBox = new HBox(5);

            {
                viewButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 10px; -fx-padding: 2 8;");
                editButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-size: 10px; -fx-padding: 2 8;");
                deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 10px; -fx-padding: 2 8;");

                buttonBox.setAlignment(Pos.CENTER);
                buttonBox.getChildren().addAll(viewButton, editButton, deleteButton);

                viewButton.setOnAction(event -> {
                    Contract contract = getTableView().getItems().get(getIndex());
                    viewContract(contract);
                });

                editButton.setOnAction(event -> {
                    Contract contract = getTableView().getItems().get(getIndex());
                    editContract(contract);
                });

                deleteButton.setOnAction(event -> {
                    Contract contract = getTableView().getItems().get(getIndex());
                    deleteContract(contract);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonBox);
                }
            }
        });

        contractTable.setItems(contractList);
    }

    private void initializeComboBoxes() {
        // Initialize contract type combo box
        contractTypeComboBox.getItems().addAll(
                "Hợp đồng không xác định thời hạn",
                "Hợp đồng xác định thời hạn 1 năm",
                "Hợp đồng xác định thời hạn 2 năm",
                "Hợp đồng thời vụ",
                "Hợp đồng thử việc"
        );

        // Initialize status combo box
        statusComboBox.getItems().addAll("Active", "Expired", "Terminated", "Renewed");
    }

    private void loadContractData() {
        List<Contract> contracts = contractService.getAllContracts();
        contractList.clear();
        contractList.addAll(contracts);
    }

    private void loadStatistics() {
        // Load general statistics
        Map<String, Integer> stats = contractService.getContractStatistics();
        activeContractsLabel.setText(String.valueOf(stats.getOrDefault("active", 0)));
        expiringContractsLabel.setText(String.valueOf(stats.getOrDefault("expiring", 0)));
        expiredContractsLabel.setText(String.valueOf(stats.getOrDefault("expired", 0)));

        // Load contract type statistics
        Map<String, Integer> typeStats = contractService.getContractTypeStatistics();

        // Calculate statistics for display
        int longTerm = typeStats.getOrDefault("Hợp đồng không xác định thời hạn", 0) +
                typeStats.getOrDefault("Hợp đồng xác định thời hạn 2 năm", 0);
        int shortTerm = typeStats.getOrDefault("Hợp đồng xác định thời hạn 1 năm", 0) +
                typeStats.getOrDefault("Hợp đồng thời vụ", 0);
        int probation = typeStats.getOrDefault("Hợp đồng thử việc", 0);

        longTermContractsLabel.setText(String.valueOf(longTerm));
        shortTermContractsLabel.setText(String.valueOf(shortTerm));
        probationContractsLabel.setText(String.valueOf(probation));
    }

    private void setupSearchAndFilter() {
        // Add listeners for real-time search and filter
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterContracts();
        });

        contractTypeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            filterContracts();
        });

        statusComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            filterContracts();
        });

        fromDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            filterContracts();
        });
    }

    private void filterContracts() {
        String searchTerm = searchField.getText();
        String contractType = contractTypeComboBox.getValue();
        String status = statusComboBox.getValue();
        LocalDate fromDate = fromDatePicker.getValue();

        List<Contract> filteredContracts = contractService.searchContracts(searchTerm, contractType, status, fromDate);
        contractList.clear();
        contractList.addAll(filteredContracts);
    }

    @FXML
    private void handleCreateContract() {
        // Open create contract dialog
        showContractDialog(null);
    }

    private void viewContract(Contract contract) {
        // Create a dialog to view contract details
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Chi tiết Hợp đồng");
        dialog.setHeaderText("Thông tin chi tiết hợp đồng: " + contract.getContractNumber());

        // Create content
        StringBuilder content = new StringBuilder();
        content.append("Mã hợp đồng: ").append(contract.getContractNumber()).append("\n");
        content.append("Nhân viên: ").append(contract.getEmployeeName()).append("\n");
        content.append("Loại hợp đồng: ").append(contract.getContractTypeName()).append("\n");
        content.append("Ngày bắt đầu: ").append(contract.getStartDate()).append("\n");
        content.append("Ngày kết thúc: ").append(contract.getEndDate() != null ? contract.getEndDate() : "Không xác định").append("\n");
        content.append("Mức lương: ").append(contract.getFormattedSalary()).append("\n");
        content.append("Phụ cấp: ").append(contract.getAllowances() != null ? String.format("%,.0f VND", contract.getAllowances()) : "0 VND").append("\n");
        content.append("Trạng thái: ").append(contract.getStatus()).append("\n");
        content.append("Ngày ký: ").append(contract.getSignedDate() != null ? contract.getSignedDate() : "Chưa ký").append("\n");
        if (contract.getBenefits() != null && !contract.getBenefits().isEmpty()) {
            content.append("Quyền lợi: ").append(contract.getBenefits()).append("\n");
        }
        if (contract.getNotes() != null && !contract.getNotes().isEmpty()) {
            content.append("Ghi chú: ").append(contract.getNotes()).append("\n");
        }

        TextArea textArea = new TextArea(content.toString());
        textArea.setEditable(false);
        textArea.setPrefRowCount(15);
        textArea.setPrefColumnCount(50);

        dialog.getDialogPane().setContent(textArea);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

        dialog.showAndWait();
    }

    private void editContract(Contract contract) {
        showContractDialog(contract);
    }

    private void deleteContract(Contract contract) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText("Xóa hợp đồng");
        alert.setContentText("Bạn có chắc chắn muốn xóa hợp đồng " + contract.getContractNumber() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (contractService.deleteContract(contract.getId())) {
                    showSuccessMessage("Xóa hợp đồng thành công!");
                    loadContractData();
                    loadStatistics();
                } else {
                    showErrorMessage("Không thể xóa hợp đồng!");
                }
            }
        });
    }

    private void showContractDialog(Contract contract) {
        Dialog<Contract> dialog = new Dialog<>();
        dialog.setTitle(contract == null ? "Tạo hợp đồng mới" : "Chỉnh sửa hợp đồng");
        dialog.setHeaderText(contract == null ? "Nhập thông tin hợp đồng mới" : "Chỉnh sửa thông tin hợp đồng");

        // Create form fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField contractNumberField = new TextField();
        contractNumberField.setPromptText("Mã hợp đồng");

        ComboBox<String> employeeComboBox = new ComboBox<>();
        loadEmployeesForComboBox(employeeComboBox);

        ComboBox<String> contractTypeComboBoxDialog = new ComboBox<>();
        contractTypeComboBoxDialog.getItems().addAll(
                "Hợp đồng không xác định thời hạn",
                "Hợp đồng xác định thời hạn 1 năm",
                "Hợp đồng xác định thời hạn 2 năm",
                "Hợp đồng thời vụ",
                "Hợp đồng thử việc"
        );

        DatePicker startDatePicker = new DatePicker();
        DatePicker endDatePicker = new DatePicker();
        TextField salaryField = new TextField();
        salaryField.setPromptText("Mức lương");
        TextField allowancesField = new TextField();
        allowancesField.setPromptText("Phụ cấp");

        ComboBox<String> statusComboBoxDialog = new ComboBox<>();
        statusComboBoxDialog.getItems().addAll("Active", "Expired", "Terminated", "Renewed");

        DatePicker signedDatePicker = new DatePicker();
        TextArea benefitsArea = new TextArea();
        benefitsArea.setPromptText("Quyền lợi");
        benefitsArea.setPrefRowCount(3);

        TextArea notesArea = new TextArea();
        notesArea.setPromptText("Ghi chú");
        notesArea.setPrefRowCount(3);

        // Populate fields if editing
        if (contract != null) {
            contractNumberField.setText(contract.getContractNumber());
            employeeComboBox.setValue(contract.getEmployeeName());
            contractTypeComboBoxDialog.setValue(contract.getContractTypeName());
            startDatePicker.setValue(contract.getStartDate());
            endDatePicker.setValue(contract.getEndDate());
            if (contract.getSalary() != null) {
                salaryField.setText(contract.getSalary().toString());
            }
            if (contract.getAllowances() != null) {
                allowancesField.setText(contract.getAllowances().toString());
            }
            statusComboBoxDialog.setValue(contract.getStatus());
            signedDatePicker.setValue(contract.getSignedDate());
            benefitsArea.setText(contract.getBenefits());
            notesArea.setText(contract.getNotes());
        } else {
            // Generate contract number for new contract
            contractNumberField.setText(contractService.generateContractNumber());
            statusComboBoxDialog.setValue("Active");
        }

        // Add fields to grid
        grid.add(new Label("Mã hợp đồng:"), 0, 0);
        grid.add(contractNumberField, 1, 0);
        grid.add(new Label("Nhân viên:"), 0, 1);
        grid.add(employeeComboBox, 1, 1);
        grid.add(new Label("Loại hợp đồng:"), 0, 2);
        grid.add(contractTypeComboBoxDialog, 1, 2);
        grid.add(new Label("Ngày bắt đầu:"), 0, 3);
        grid.add(startDatePicker, 1, 3);
        grid.add(new Label("Ngày kết thúc:"), 0, 4);
        grid.add(endDatePicker, 1, 4);
        grid.add(new Label("Mức lương:"), 0, 5);
        grid.add(salaryField, 1, 5);
        grid.add(new Label("Phụ cấp:"), 0, 6);
        grid.add(allowancesField, 1, 6);
        grid.add(new Label("Trạng thái:"), 0, 7);
        grid.add(statusComboBoxDialog, 1, 7);
        grid.add(new Label("Ngày ký:"), 0, 8);
        grid.add(signedDatePicker, 1, 8);
        grid.add(new Label("Quyền lợi:"), 0, 9);
        grid.add(benefitsArea, 1, 9);
        grid.add(new Label("Ghi chú:"), 0, 10);
        grid.add(notesArea, 1, 10);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveButtonType = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Enable/disable save button based on required fields
        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        // Add validation
        contractNumberField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty() || employeeComboBox.getValue() == null ||
                    contractTypeComboBoxDialog.getValue() == null || startDatePicker.getValue() == null);
        });

        employeeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(contractNumberField.getText().trim().isEmpty() || newValue == null ||
                    contractTypeComboBoxDialog.getValue() == null || startDatePicker.getValue() == null);
        });

        contractTypeComboBoxDialog.valueProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(contractNumberField.getText().trim().isEmpty() || employeeComboBox.getValue() == null ||
                    newValue == null || startDatePicker.getValue() == null);
        });

        startDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(contractNumberField.getText().trim().isEmpty() || employeeComboBox.getValue() == null ||
                    contractTypeComboBoxDialog.getValue() == null || newValue == null);
        });

        // Convert the result when save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Contract newContract = contract != null ? contract : new Contract();

                newContract.setContractNumber(contractNumberField.getText().trim());

                // Get employee ID from name
                String selectedEmployeeName = employeeComboBox.getValue();
                int employeeId = getEmployeeIdByName(selectedEmployeeName);
                newContract.setEmployeeId(employeeId);
                newContract.setEmployeeName(selectedEmployeeName);

                // Get contract type ID from name
                String selectedContractType = contractTypeComboBoxDialog.getValue();
                int contractTypeId = getContractTypeIdByName(selectedContractType);
                newContract.setContractTypeId(contractTypeId);
                newContract.setContractTypeName(selectedContractType);

                newContract.setStartDate(startDatePicker.getValue());
                newContract.setEndDate(endDatePicker.getValue());

                try {
                    if (!salaryField.getText().trim().isEmpty()) {
                        newContract.setSalary(new BigDecimal(salaryField.getText().trim()));
                    }
                    if (!allowancesField.getText().trim().isEmpty()) {
                        newContract.setAllowances(new BigDecimal(allowancesField.getText().trim()));
                    }
                } catch (NumberFormatException e) {
                    showErrorMessage("Vui lòng nhập số hợp lệ cho lương và phụ cấp!");
                    return null;
                }

                newContract.setStatus(statusComboBoxDialog.getValue());
                newContract.setSignedDate(signedDatePicker.getValue());
                newContract.setBenefits(benefitsArea.getText().trim());
                newContract.setNotes(notesArea.getText().trim());

                // Set created_by (you should get this from current user session)
                newContract.setCreatedBy(1); // Placeholder - replace with actual user ID

                return newContract;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            if (result != null) {
                boolean success;
                if (contract == null) {
                    success = contractService.addContract(result);
                    if (success) {
                        showSuccessMessage("Tạo hợp đồng thành công!");
                    }
                } else {
                    success = contractService.updateContract(result);
                    if (success) {
                        showSuccessMessage("Cập nhật hợp đồng thành công!");
                    }
                }

                if (success) {
                    loadContractData();
                    loadStatistics();
                } else {
                    showErrorMessage("Có lỗi xảy ra khi lưu hợp đồng!");
                }
            }
        });
    }

    // Fixed method: Compatible with Employee model structure
    private void loadEmployeesForComboBox(ComboBox<String> comboBox) {
        try {
            List<Employee> employees = employeeService.getAllEmployees();
            comboBox.getItems().clear();

            for (Employee employee : employees) {
                // Create display name as "FirstName LastName"
                String fullName = employee.getFullName();
                comboBox.getItems().add(fullName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback if there's an error
            comboBox.getItems().addAll("Không thể tải danh sách nhân viên");
        }
    }

    // Fixed method: Compatible with Employee model structure
    private int getEmployeeIdByName(String employeeName) {
        try {
            List<Employee> employees = employeeService.getAllEmployees();

            for (Employee employee : employees) {
                String fullName = employee.getFullName();
                if (fullName.equals(employeeName)) {
                    return employee.getId();
                }
            }

            // If not found, return 0 or handle error
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0; // Fallback
        }
    }

    private int getContractTypeIdByName(String contractTypeName) {
        // Map contract type names to IDs based on your database
        switch (contractTypeName) {
            case "Hợp đồng không xác định thời hạn":
                return 1;
            case "Hợp đồng xác định thời hạn 1 năm":
                return 2;
            case "Hợp đồng xác định thời hạn 2 năm":
                return 3;
            case "Hợp đồng thời vụ":
                return 4;
            case "Hợp đồng thử việc":
                return 5;
            default:
                return 1;
        }
    }

    private void showSuccessMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thành công");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleRefresh() {
        loadContractData();
        loadStatistics();

        // Clear filters
        searchField.clear();
        contractTypeComboBox.setValue(null);
        statusComboBox.setValue(null);
        fromDatePicker.setValue(null);
    }

    @FXML
    private void handleExportContracts() {
        // Implement export functionality
        showSuccessMessage("Chức năng xuất file đang được phát triển!");
    }
}