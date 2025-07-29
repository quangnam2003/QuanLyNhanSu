package com.service;

import com.model.Department;
import com.utils.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentService {

    public List<Department> getAllDepartments() {
        List<Department> departments = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT * FROM departments";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Department dept = new Department();
                dept.setId(rs.getInt("id"));
                dept.setDepartmentName(rs.getString("department_name"));
                departments.add(dept);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return departments;
    }

    public List<String> getAllDepartmentNames() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT department_name FROM departments";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getString("department_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Department getDepartmentById(int id) {
        for (Department d : getAllDepartments()) {
            if (d.getId() == id) return d;
        }
        return null;
    }

}
