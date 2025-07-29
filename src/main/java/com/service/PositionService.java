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
                pos.setPositionName(rs.getString("position_name"));
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

    public List<Position> getPositionsByDepartmentId(int departmentId) {
        List<Position> positions = new ArrayList<>();
        String query = "SELECT * FROM positions WHERE department_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, departmentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Position p = new Position();
                p.setId(rs.getInt("id"));
                p.setPositionName(rs.getString("position_name"));
                p.setPositionCode(rs.getString("position_code"));
                // bổ sung nếu cần
                positions.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return positions;
    }

}
