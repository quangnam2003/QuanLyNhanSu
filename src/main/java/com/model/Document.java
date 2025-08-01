package com.model;

import java.time.LocalDateTime;

public class Document {
    private int id;
    private String title;
    private String fileName;
    private String fileUrl;
    private String documentType;
    private LocalDateTime uploadedAt;

    // Constructor không tham số
    public Document() {
    }

    // Constructor đầy đủ
    public Document(int id, String title, String fileName, String fileUrl, String documentType, LocalDateTime uploadedAt) {
        this.id = id;
        this.title = title;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.documentType = documentType;
        this.uploadedAt = uploadedAt;
    }

    // Getters và Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    @Override
    public String toString() {
        return "Document{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", documentType='" + documentType + '\'' +
                ", uploadedAt=" + uploadedAt +
                '}';
    }
}