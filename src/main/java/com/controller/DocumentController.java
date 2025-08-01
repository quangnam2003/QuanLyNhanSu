package com.controller;

import com.model.Document;
import com.service.DocumentService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class DocumentController implements Initializable {

    @FXML private TableView<Document> documentTable;
    @FXML private TableColumn<Document, Integer> colId;
    @FXML private TableColumn<Document, String> colTitle;
    @FXML private TableColumn<Document, String> colFileName;
    @FXML private TableColumn<Document, String> colFileUrl;
    @FXML private TableColumn<Document, String> colDocumentType;
    @FXML private TableColumn<Document, String> colUploadedAt;
    @FXML private TableColumn<Document, Void> actionColumn;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> documentTypeFilter;
    @FXML private Button btnAddDocument;

    @FXML private Label lblTotal;

    private final DocumentService documentService = new DocumentService();
    private ObservableList<Document> documentList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadDocumentData();
        loadDocumentTypeFilter();
        setupSearchAndFilter();
        setupActionColumn();
        updateStatistics();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colFileName.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        colFileUrl.setCellValueFactory(new PropertyValueFactory<>("fileUrl"));
        colDocumentType.setCellValueFactory(new PropertyValueFactory<>("documentType"));
        
        // Format uploaded date
        colUploadedAt.setCellValueFactory(cellData -> {
            LocalDateTime uploadedAt = cellData.getValue().getUploadedAt();
            if (uploadedAt != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                return new SimpleStringProperty(uploadedAt.format(formatter));
            }
            return new SimpleStringProperty("");
        });

        // Set column widths
        colId.setPrefWidth(80);
        colTitle.setPrefWidth(200);
        colFileName.setPrefWidth(150);
        colFileUrl.setPrefWidth(250);
        colDocumentType.setPrefWidth(120);
        colUploadedAt.setPrefWidth(150);
        actionColumn.setPrefWidth(150);
    }

    private void setupActionColumn() {
        actionColumn.setCellFactory(new Callback<TableColumn<Document, Void>, TableCell<Document, Void>>() {
            @Override
            public TableCell<Document, Void> call(final TableColumn<Document, Void> param) {
                return new TableCell<>() {
                    private final Button btnView = new Button("Xem");
                    private final Button btnEdit = new Button("Sửa");
                    private final Button btnDelete = new Button("Xóa");
                    private final HBox hBox = new HBox(btnView, btnEdit, btnDelete);

                    {
                        btnView.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 10;");
                        btnEdit.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 10;");
                        btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 10;");

                        hBox.setSpacing(5);
                        hBox.setAlignment(Pos.CENTER);

                        btnView.setOnAction(event -> {
                            Document document = getTableView().getItems().get(getIndex());
                            handleView(document);
                        });

                        btnEdit.setOnAction(event -> {
                            Document document = getTableView().getItems().get(getIndex());
                            handleEdit(document);
                        });

                        btnDelete.setOnAction(event -> {
                            Document document = getTableView().getItems().get(getIndex());
                            handleDelete(document);
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

    private void setupSearchAndFilter() {
        // Search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterDocuments();
        });

        // Filter functionality
        documentTypeFilter.setOnAction(e -> filterDocuments());
    }

    private void loadDocumentData() {
        List<Document> documents = documentService.getAllDocuments();
        documentList.clear();
        documentList.addAll(documents);
        documentTable.setItems(documentList);
    }

    private void loadDocumentTypeFilter() {
        List<String> documentTypes = documentService.getAllDocumentTypes();
        documentTypes.add(0, "Tất cả");
        documentTypeFilter.setItems(FXCollections.observableArrayList(documentTypes));
        documentTypeFilter.setValue("Tất cả");
    }

    private void filterDocuments() {
        String keyword = searchField.getText();
        String selectedType = documentTypeFilter.getValue();
        
        List<Document> filteredDocuments = documentService.searchDocuments(keyword, selectedType);
        documentList.clear();
        documentList.addAll(filteredDocuments);
        documentTable.setItems(documentList);
        updateStatistics();
    }

    private void updateStatistics() {
        lblTotal.setText(String.valueOf(documentList.size()));
    }

    @FXML
    private void handleAddDocument() {
        showAddDocumentDialog();
    }

    private void handleEdit(Document document) {
        showEditDocumentDialog(document);
    }

    private void handleView(Document document) {
        // Open document URL in browser
        if (document.getFileUrl() != null && !document.getFileUrl().isEmpty()) {
            try {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(URI.create(document.getFileUrl()));
                } else {
                    showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Hệ thống không hỗ trợ mở URL trực tiếp!");
                }
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể mở tài liệu!");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "URL tài liệu không hợp lệ!");
        }
    }

    private void handleDelete(Document document) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText(null);
        alert.setContentText("Bạn có chắc chắn muốn xóa tài liệu \"" + document.getTitle() + "\" không?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = documentService.deleteDocument(document.getId());
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Xóa tài liệu thành công!");
                loadDocumentData();
                updateStatistics();
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa tài liệu!");
            }
        }
    }

    private void showAddDocumentDialog() {
        Dialog<Document> dialog = new Dialog<>();
        dialog.setTitle("Thêm tài liệu mới");
        dialog.setHeaderText("Vui lòng nhập thông tin tài liệu");

        // Set button types
        ButtonType saveButtonType = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create form fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField();
        titleField.setPromptText("Tiêu đề tài liệu");
        TextField fileNameField = new TextField();
        fileNameField.setPromptText("Tên file");
        TextField fileUrlField = new TextField();
        fileUrlField.setPromptText("URL tài liệu");
        ComboBox<String> documentTypeCombo = new ComboBox<>();
        
        // Load document types
        List<String> documentTypes = documentService.getAllDocumentTypes();
        documentTypes.addAll(List.of("Policy", "HR", "Finance", "Training", "General"));
        documentTypeCombo.setItems(FXCollections.observableArrayList(documentTypes));
        documentTypeCombo.setPromptText("Chọn loại tài liệu");

        grid.add(new Label("Tiêu đề:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Tên file:"), 0, 1);
        grid.add(fileNameField, 1, 1);
        grid.add(new Label("URL:"), 0, 2);
        grid.add(fileUrlField, 1, 2);
        grid.add(new Label("Loại tài liệu:"), 0, 3);
        grid.add(documentTypeCombo, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Enable/Disable save button depending on whether fields are filled
        javafx.scene.Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        // Do some validation (disable save button if required fields are empty)
        titleField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty() || fileNameField.getText().trim().isEmpty() 
                || fileUrlField.getText().trim().isEmpty() || documentTypeCombo.getValue() == null);
        });
        fileNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty() || titleField.getText().trim().isEmpty() 
                || fileUrlField.getText().trim().isEmpty() || documentTypeCombo.getValue() == null);
        });
        fileUrlField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty() || titleField.getText().trim().isEmpty() 
                || fileNameField.getText().trim().isEmpty() || documentTypeCombo.getValue() == null);
        });
        documentTypeCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue == null || titleField.getText().trim().isEmpty() 
                || fileNameField.getText().trim().isEmpty() || fileUrlField.getText().trim().isEmpty());
        });

        // Convert the result to a document when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Document document = new Document();
                document.setTitle(titleField.getText().trim());
                document.setFileName(fileNameField.getText().trim());
                document.setFileUrl(fileUrlField.getText().trim());
                document.setDocumentType(documentTypeCombo.getValue());
                document.setUploadedAt(LocalDateTime.now());
                return document;
            }
            return null;
        });

        Optional<Document> result = dialog.showAndWait();
        result.ifPresent(document -> {
            boolean success = documentService.addDocument(document);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Thêm tài liệu thành công!");
                loadDocumentData();
                updateStatistics();
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể thêm tài liệu!");
            }
        });
    }

    private void showEditDocumentDialog(Document document) {
        Dialog<Document> dialog = new Dialog<>();
        dialog.setTitle("Sửa tài liệu");
        dialog.setHeaderText("Chỉnh sửa thông tin tài liệu");

        // Set button types
        ButtonType saveButtonType = new ButtonType("Cập nhật", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create form fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField(document.getTitle());
        TextField fileNameField = new TextField(document.getFileName());
        TextField fileUrlField = new TextField(document.getFileUrl());
        ComboBox<String> documentTypeCombo = new ComboBox<>();
        
        // Load document types
        List<String> documentTypes = documentService.getAllDocumentTypes();
        documentTypes.addAll(List.of("Policy", "HR", "Finance", "Training", "General"));
        documentTypeCombo.setItems(FXCollections.observableArrayList(documentTypes));
        documentTypeCombo.setValue(document.getDocumentType());

        grid.add(new Label("Tiêu đề:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Tên file:"), 0, 1);
        grid.add(fileNameField, 1, 1);
        grid.add(new Label("URL:"), 0, 2);
        grid.add(fileUrlField, 1, 2);
        grid.add(new Label("Loại tài liệu:"), 0, 3);
        grid.add(documentTypeCombo, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Enable/Disable save button depending on whether fields are filled
        javafx.scene.Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        
        // Do some validation (disable save button if required fields are empty)
        titleField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty() || fileNameField.getText().trim().isEmpty() 
                || fileUrlField.getText().trim().isEmpty() || documentTypeCombo.getValue() == null);
        });
        fileNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty() || titleField.getText().trim().isEmpty() 
                || fileUrlField.getText().trim().isEmpty() || documentTypeCombo.getValue() == null);
        });
        fileUrlField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty() || titleField.getText().trim().isEmpty() 
                || fileNameField.getText().trim().isEmpty() || documentTypeCombo.getValue() == null);
        });
        documentTypeCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue == null || titleField.getText().trim().isEmpty() 
                || fileNameField.getText().trim().isEmpty() || fileUrlField.getText().trim().isEmpty());
        });

        // Convert the result to a document when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                document.setTitle(titleField.getText().trim());
                document.setFileName(fileNameField.getText().trim());
                document.setFileUrl(fileUrlField.getText().trim());
                document.setDocumentType(documentTypeCombo.getValue());
                return document;
            }
            return null;
        });

        Optional<Document> result = dialog.showAndWait();
        result.ifPresent(updatedDocument -> {
            boolean success = documentService.updateDocument(updatedDocument);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Cập nhật tài liệu thành công!");
                loadDocumentData();
                updateStatistics();
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể cập nhật tài liệu!");
            }
        });
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Hover effects for add button
    @FXML
    private void onHoverAdd() {
        btnAddDocument.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 5; -fx-cursor: hand;");
    }

    @FXML
    private void onExitAdd() {
        btnAddDocument.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 5; -fx-cursor: hand;");
    }
}