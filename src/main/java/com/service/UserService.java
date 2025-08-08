package com.service;

import com.model.User;
import com.model.Role;
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

    /**
     * Lấy tất cả người dùng kèm thông tin vai trò
     */
    public java.util.List<User> getAllUsersWithRoles() {
        String sql = "SELECT u.id, u.username, u.password, u.role_id, r.role_name, r.role_code " +
                "FROM users u LEFT JOIN roles r ON u.role_id = r.id ORDER BY u.id";
        java.util.List<User> users = new java.util.ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getInt("role_id"),
                        rs.getString("role_name"),
                        rs.getString("role_code")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Thống kê số lượng người dùng theo role_code
     */
    public java.util.Map<String, Integer> getUserStatsByRole() {
        String sql = "SELECT r.role_code, COUNT(*) AS total FROM users u " +
                "LEFT JOIN roles r ON u.role_id = r.id GROUP BY r.role_code";
        java.util.Map<String, Integer> stats = new java.util.HashMap<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String role = rs.getString("role_code");
                int total = rs.getInt("total");
                if (role == null) role = "UNKNOWN";
                stats.put(role, total);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    /**
     * Xoá user theo id
     */
    public boolean deleteUserById(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ===== New CRUD helpers for Settings page =====

    public java.util.List<Role> getAllRoles() {
        String sql = "SELECT id, role_name, role_code FROM roles ORDER BY id";
        java.util.List<Role> roles = new java.util.ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                roles.add(new Role(
                        rs.getInt("id"),
                        rs.getString("role_name"),
                        rs.getString("role_code")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }

    public java.util.List<Role> getRolesForUser() {
        String sql = "SELECT id, role_name, role_code FROM roles WHERE id <> 6 ORDER BY id;";
        java.util.List<Role> roles = new java.util.ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                roles.add(new Role(
                        rs.getInt("id"),
                        rs.getString("role_name"),
                        rs.getString("role_code")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }

    public boolean addUser(String username, String password, int roleId) {
        String sql = "INSERT INTO users (username, password, role_id) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setInt(3, roleId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateUser(int id, String username, String password, int roleId) {
        StringBuilder sql = new StringBuilder("UPDATE users SET username = ?, role_id = ?");
        boolean hasPassword = password != null && !password.isEmpty();
        if (hasPassword) sql.append(", password = ?");
        sql.append(" WHERE id = ?");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            ps.setString(idx++, username);
            ps.setInt(idx++, roleId);
            if (hasPassword) ps.setString(idx++, password);
            ps.setInt(idx, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isUsernameExists(String username, Integer excludeUserId) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?" +
                (excludeUserId != null ? " AND id <> ?" : "");
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            if (excludeUserId != null) {
                ps.setInt(2, excludeUserId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}