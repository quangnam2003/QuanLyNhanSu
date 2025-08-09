package com.service;

import com.model.Position;
import com.utils.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PositionService {

    public List<Position> getAllPositions() {
        List<Position> positions = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT * FROM positions";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Position pos = new Position();
                pos.setId(rs.getInt("id"));
                pos.setPositionCode(getSafeString(rs, "position_code"));
                pos.setPositionName(rs.getString("position_name"));
                // IMPORTANT: map department_id so filtering works
                pos.setDepartmentId(getSafeInt(rs, "department_id"));
                pos.setLevel(getSafeInt(rs, "level"));
                pos.setDescription(getSafeString(rs, "description"));
                pos.setRequirements(getSafeString(rs, "requirements"));
                positions.add(pos);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return positions;
    }

    public List<String> getAllPositionNames() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT position_name FROM positions";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getString("position_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<String> getPositionNamesByDepartment(String departmentName) {
        List<String> list = new ArrayList<>();
        String sql = """
        SELECT p.position_name
        FROM positions p
        JOIN departments d ON p.department_id = d.id
        WHERE d.department_name = ?
    """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, departmentName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("position_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public Position getPositionById(int id) {
        for (Position p : getAllPositions()) {
            if (p.getId() == id) return p;
        }
        return null;
    }

    public List<Position> getPositionsByDepartmentId(int deptId) {
        List<Position> positions = new ArrayList<>();
        String sql = "SELECT * FROM positions WHERE department_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, deptId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Position pos = new Position();
                pos.setId(rs.getInt("id"));
                pos.setPositionCode(getSafeString(rs, "position_code"));
                pos.setPositionName(rs.getString("position_name"));
                pos.setDepartmentId(getSafeInt(rs, "department_id"));
                pos.setLevel(getSafeInt(rs, "level"));
                pos.setDescription(getSafeString(rs, "description"));
                pos.setRequirements(getSafeString(rs, "requirements"));
                positions.add(pos);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return positions;
    }

    private String getSafeString(ResultSet rs, String column) throws SQLException {
        String value = rs.getString(column);
        return value == null ? "" : value;
    }

    private int getSafeInt(ResultSet rs, String column) throws SQLException {
        int value = rs.getInt(column);
        if (rs.wasNull()) return 0;
        return value;
    }
}
