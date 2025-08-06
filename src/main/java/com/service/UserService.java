package com.service;

import com.model.User;
import com.utils.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserService {
    
    public User authenticateUser(String username, String password) {
        String sql = "SELECT u.id, u.username, u.password, u.role_id, r.role_name, r.role_code " +
                    "FROM users u " +
                    "LEFT JOIN roles r ON u.role_id = r.id " +
                    "WHERE u.username = ? AND u.password = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getInt("role_id"),
                        rs.getString("role_name"),
                        rs.getString("role_code")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean hasPermission(int userId, String permissionCode) {
        String sql = "SELECT COUNT(*) as count " +
                    "FROM users u " +
                    "JOIN role_permissions rp ON u.role_id = rp.role_id " +
                    "JOIN permissions p ON rp.permission_id = p.id " +
                    "WHERE u.id = ? AND p.permission_code = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setString(2, permissionCode);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean isAdmin(User user) {
        return user != null && "ADMIN".equals(user.getRoleCode());
    }
    
    public boolean isHR(User user) {
        return user != null && "HR".equals(user.getRoleCode());
    }
    
    public boolean isDeptManager(User user) {
        return user != null && "DEPT_MANAGER".equals(user.getRoleCode());
    }
} 