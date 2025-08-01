package com.service;

import com.model.Document;
import com.utils.DBUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DocumentService {

    public List<Document> getAllDocuments() {
        List<Document> documents = new ArrayList<>();

        String sql = "SELECT * FROM documents ORDER BY uploaded_at DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Document document = new Document();
                document.setId(rs.getInt("id"));
                document.setTitle(rs.getString("title"));
                document.setFileName(rs.getString("file_name"));
                document.setFileUrl(rs.getString("file_url"));
                document.setDocumentType(rs.getString("document_type"));
                
                Timestamp timestamp = rs.getTimestamp("uploaded_at");
                if (timestamp != null) {
                    document.setUploadedAt(timestamp.toLocalDateTime());
                }

                documents.add(document);
            }

        } catch (SQLException e) {
            System.err.println("Error getting all documents: " + e.getMessage());
            e.printStackTrace();
        }

        return documents;
    }

    public boolean addDocument(Document document) {
        String sql = "INSERT INTO documents (title, file_name, file_url, document_type, uploaded_at) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, document.getTitle());
            stmt.setString(2, document.getFileName());
            stmt.setString(3, document.getFileUrl());
            stmt.setString(4, document.getDocumentType());
            stmt.setTimestamp(5, Timestamp.valueOf(document.getUploadedAt()));

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error adding document: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateDocument(Document document) {
        String sql = "UPDATE documents SET title = ?, file_name = ?, file_url = ?, document_type = ? WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, document.getTitle());
            stmt.setString(2, document.getFileName());
            stmt.setString(3, document.getFileUrl());
            stmt.setString(4, document.getDocumentType());
            stmt.setInt(5, document.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating document: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteDocument(int documentId) {
        String sql = "DELETE FROM documents WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, documentId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting document: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Document getDocumentById(int id) {
        String sql = "SELECT * FROM documents WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Document document = new Document();
                document.setId(rs.getInt("id"));
                document.setTitle(rs.getString("title"));
                document.setFileName(rs.getString("file_name"));
                document.setFileUrl(rs.getString("file_url"));
                document.setDocumentType(rs.getString("document_type"));
                
                Timestamp timestamp = rs.getTimestamp("uploaded_at");
                if (timestamp != null) {
                    document.setUploadedAt(timestamp.toLocalDateTime());
                }

                return document;
            }

        } catch (SQLException e) {
            System.err.println("Error getting document by id: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public List<String> getAllDocumentTypes() {
        List<String> types = new ArrayList<>();
        String sql = "SELECT DISTINCT document_type FROM documents WHERE document_type IS NOT NULL ORDER BY document_type";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                types.add(rs.getString("document_type"));
            }

        } catch (SQLException e) {
            System.err.println("Error getting document types: " + e.getMessage());
            e.printStackTrace();
        }

        return types;
    }

    public List<Document> searchDocuments(String keyword, String documentType) {
        List<Document> documents = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM documents WHERE 1=1");
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (title LIKE ? OR file_name LIKE ?)");
        }
        
        if (documentType != null && !documentType.trim().isEmpty() && !documentType.equals("Tất cả")) {
            sql.append(" AND document_type = ?");
        }
        
        sql.append(" ORDER BY uploaded_at DESC");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchKeyword = "%" + keyword.trim() + "%";
                stmt.setString(paramIndex++, searchKeyword);
                stmt.setString(paramIndex++, searchKeyword);
            }
            
            if (documentType != null && !documentType.trim().isEmpty() && !documentType.equals("Tất cả")) {
                stmt.setString(paramIndex, documentType);
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Document document = new Document();
                document.setId(rs.getInt("id"));
                document.setTitle(rs.getString("title"));
                document.setFileName(rs.getString("file_name"));
                document.setFileUrl(rs.getString("file_url"));
                document.setDocumentType(rs.getString("document_type"));
                
                Timestamp timestamp = rs.getTimestamp("uploaded_at");
                if (timestamp != null) {
                    document.setUploadedAt(timestamp.toLocalDateTime());
                }

                documents.add(document);
            }

        } catch (SQLException e) {
            System.err.println("Error searching documents: " + e.getMessage());
            e.printStackTrace();
        }

        return documents;
    }
}