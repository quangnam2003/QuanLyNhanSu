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

        String sql = "SELECT id, title, file_name, category, last_updated FROM documents ORDER BY last_updated DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Document document = new Document();
                document.setId(rs.getInt("id"));
                document.setTitle(rs.getString("title"));
                document.setFileName(rs.getString("file_name"));
                document.setCategory(rs.getString("category"));

                Timestamp timestamp = rs.getTimestamp("last_updated");
                if (timestamp != null) {
                    document.setLastUpdated(timestamp.toLocalDateTime());
                }

                documents.add(document);
            }

        } catch (SQLException e) {
            System.err.println("Error getting all documents: " + e.getMessage());
            e.printStackTrace();
        }

        return documents;
    }

    public String addDocument(Document document) {
        String sql = "INSERT INTO documents (title, file_name, file_data, category) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, document.getTitle());
            stmt.setString(2, document.getFileName());
            stmt.setBytes(3, document.getFileData());
            stmt.setString(4, document.getCategory());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return "SUCCESS";
            } else {
                return "Không có dòng nào được thêm vào database.";
            }

        } catch (SQLException e) {
            System.err.println("Error adding document: " + e.getMessage());
            e.printStackTrace();

            if (e.getMessage().contains("Packet for query is too large")) {
                return "File quá lớn cho cấu hình database hiện tại.";
            } else if (e.getMessage().contains("doesn't exist")) {
                return "Bảng 'documents' không tồn tại trong database.";
            } else {
                return "Lỗi database: " + e.getMessage();
            }
        }
    }

    public String updateDocument(Document document) {
        String sql = "UPDATE documents SET title = ?, file_name = ?, file_data = ?, category = ? WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, document.getTitle());
            stmt.setString(2, document.getFileName());
            stmt.setBytes(3, document.getFileData());
            stmt.setString(4, document.getCategory());
            stmt.setInt(5, document.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return "SUCCESS";
            } else {
                return "Không tìm thấy tài liệu để cập nhật.";
            }

        } catch (SQLException e) {
            System.err.println("Error updating document: " + e.getMessage());
            e.printStackTrace();

            if (e.getMessage().contains("Packet for query is too large")) {
                return "File quá lớn cho cấu hình database hiện tại.";
            } else {
                return "Lỗi database: " + e.getMessage();
            }
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
                document.setFileData(rs.getBytes("file_data"));
                document.setCategory(rs.getString("category"));

                Timestamp timestamp = rs.getTimestamp("last_updated");
                if (timestamp != null) {
                    document.setLastUpdated(timestamp.toLocalDateTime());
                }

                return document;
            }

        } catch (SQLException e) {
            System.err.println("Error getting document by id: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT category FROM documents WHERE category IS NOT NULL ORDER BY category";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                categories.add(rs.getString("category"));
            }

        } catch (SQLException e) {
            System.err.println("Error getting document categories: " + e.getMessage());
            e.printStackTrace();
        }

        return categories;
    }

    public List<Document> searchDocuments(String keyword, String category) {
        List<Document> documents = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT id, title, file_name, category, last_updated FROM documents WHERE 1=1");

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (title LIKE ? OR file_name LIKE ?)");
        }

        if (category != null && !category.trim().isEmpty() && !category.equals("Tất cả")) {
            sql.append(" AND category = ?");
        }

        sql.append(" ORDER BY last_updated DESC");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;

            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchKeyword = "%" + keyword.trim() + "%";
                stmt.setString(paramIndex++, searchKeyword);
                stmt.setString(paramIndex++, searchKeyword);
            }

            if (category != null && !category.trim().isEmpty() && !category.equals("Tất cả")) {
                stmt.setString(paramIndex, category);
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Document document = new Document();
                document.setId(rs.getInt("id"));
                document.setTitle(rs.getString("title"));
                document.setFileName(rs.getString("file_name"));
                document.setCategory(rs.getString("category"));

                Timestamp timestamp = rs.getTimestamp("last_updated");
                if (timestamp != null) {
                    document.setLastUpdated(timestamp.toLocalDateTime());
                }

                documents.add(document);
            }

        } catch (SQLException e) {
            System.err.println("Error searching documents: " + e.getMessage());
            e.printStackTrace();
        }

        return documents;
    }

    public byte[] getDocumentFile(int id) {
        String sql = "SELECT file_data FROM documents WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getBytes("file_data");
            }

        } catch (SQLException e) {
            System.err.println("Error getting document file: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }


}