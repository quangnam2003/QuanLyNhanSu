package com.service;

import com.model.Employee;
import com.utils.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeService {

    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();

        String sql = "SELECT e.*, d.department_name, p.position_name " +
                "FROM employees e " +
                "LEFT JOIN departments d ON e.department_id = d.id " +
                "LEFT JOIN positions p ON e.position_id = p.id";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Employee employee = new Employee();

                employee.setId(rs.getInt("id"));
                employee.setFirstName(rs.getString("first_name"));
                employee.setLastName(rs.getString("last_name"));
                employee.setEmail(rs.getString("email"));
                employee.setPhone(rs.getString("phone"));
                employee.setCitizenId(rs.getString("citizen_id"));

                Date dobSql = rs.getDate("date_of_birth");
                if (dobSql != null) {
                    employee.setDateOfBirth(dobSql.toLocalDate());
                }

                employee.setGender(rs.getString("gender"));
                employee.setAddress(rs.getString("address"));
                employee.setAvatarUrl(rs.getString("avatar_url"));
                employee.setDepartmentId(rs.getInt("department_id"));
                employee.setPositionId(rs.getInt("position_id"));
                employee.setRoleId(rs.getInt("role_id"));
                employee.setManagerId(rs.getInt("manager_id"));

                Date hireDateSql = rs.getDate("hire_date");
                if (hireDateSql != null) {
                    employee.setHireDate(hireDateSql.toLocalDate());
                }

                employee.setEmploymentStatus(rs.getString("employment_status"));
                employee.setSalaryGrade(rs.getFloat("salary_grade"));
                employee.setEmergencyContactName(rs.getString("emergency_contact_name"));
                employee.setEmergencyContactPhone(rs.getString("emergency_contact_phone"));
                employee.setEmergencyContactRelationship(rs.getString("emergency_contact_relationship"));
                employee.setNotes(rs.getString("notes"));

                // Thêm thông tin từ bảng liên kết
                employee.setDepartmentName(rs.getString("department_name"));
                employee.setPositionName(rs.getString("position_name"));

                employees.add(employee);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return employees;
    }

    public List<Employee> getFilteredEmployees(String departmentName, String positionName) {
        List<Employee> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
            SELECT e.*, d.department_name, p.position_name
            FROM employees e
            LEFT JOIN departments d ON e.department_id = d.id
            LEFT JOIN positions p ON e.position_id = p.id
            WHERE 1=1
        """);

        // Thêm điều kiện nếu có lọc theo phòng ban
        if (departmentName != null && !departmentName.equals("Tất cả")) {
            sql.append(" AND d.department_name = ?");
        }

        // Thêm điều kiện nếu có lọc theo chức vụ
        if (positionName != null && !positionName.equals("Tất cả")) {
            sql.append(" AND p.position_name = ?");
        }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int index = 1;

            if (departmentName != null && !departmentName.equals("Tất cả")) {
                ps.setString(index++, departmentName);
            }

            if (positionName != null && !positionName.equals("Tất cả")) {
                ps.setString(index++, positionName);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Employee employee = new Employee();
                employee.setId(rs.getInt("id"));
                employee.setFirstName(rs.getString("first_name"));
                employee.setLastName(rs.getString("last_name"));
                employee.setEmail(rs.getString("email"));
                employee.setPhone(rs.getString("phone"));
                employee.setGender(rs.getString("gender"));
                employee.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
                employee.setHireDate(rs.getDate("hire_date").toLocalDate());
                employee.setEmploymentStatus(rs.getString("employment_status"));
                employee.setSalaryGrade(rs.getFloat("salary_grade"));
                employee.setDepartmentName(rs.getString("department_name"));
                employee.setPositionName(rs.getString("position_name"));
                list.add(employee);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }


    public List<Employee> searchEmployees(String keyword, Integer departmentId, Integer positionId) {
        List<Employee> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM employees WHERE 1=1");

        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND (full_name LIKE ? OR email LIKE ?)");
        }
        if (departmentId != null) {
            sql.append(" AND department_id = ?");
        }
        if (positionId != null) {
            sql.append(" AND position_id = ?");
        }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int index = 1;

            if (keyword != null && !keyword.isEmpty()) {
                ps.setString(index++, "%" + keyword + "%");
                ps.setString(index++, "%" + keyword + "%");
            }
            if (departmentId != null) {
                ps.setInt(index++, departmentId);
            }
            if (positionId != null) {
                ps.setInt(index++, positionId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Employee emp = new Employee();
                    emp.setId(rs.getInt("id"));
                    emp.setFirstName(rs.getString("first_name"));
                    emp.setLastName(rs.getString("last_name"));
                    emp.setEmail(rs.getString("email"));
                    emp.setPhone(rs.getString("phone"));
                    emp.setCitizenId(rs.getString("citizen_id"));
                    emp.setDateOfBirth(rs.getDate("date_of_birth") != null ?
                            rs.getDate("date_of_birth").toLocalDate() : null);
                    emp.setGender(rs.getString("gender"));
                    emp.setAddress(rs.getString("address"));
                    emp.setAvatarUrl(rs.getString("avatar_url"));
                    emp.setDepartmentId(rs.getInt("department_id"));
                    emp.setPositionId(rs.getInt("position_id"));
                    emp.setRoleId(rs.getInt("role_id"));
                    emp.setManagerId(rs.getInt("manager_id"));
                    emp.setHireDate(rs.getDate("hire_date") != null ?
                            rs.getDate("hire_date").toLocalDate() : null);
                    emp.setEmploymentStatus(rs.getString("employment_status"));
                    emp.setSalaryGrade(rs.getFloat("salary_grade"));
                    emp.setEmergencyContactName(rs.getString("emergency_contact_name"));
                    emp.setEmergencyContactPhone(rs.getString("emergency_contact_phone"));
                    emp.setEmergencyContactRelationship(rs.getString("emergency_contact_relationship"));
                    emp.setNotes(rs.getString("notes"));

                    list.add(emp);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public Map<String, Integer> getEmployeeStats() {
        Map<String, Integer> stats = new HashMap<>();

        String totalSql = "SELECT COUNT(*) FROM employees";
        String workingSql = "SELECT COUNT(*) FROM employees WHERE employment_status = 'Active'";
        String inactiveSql = "SELECT COUNT(*) FROM employees WHERE employment_status IN ('Inactive', 'Terminated')";

        try (Connection conn = DBUtil.getConnection()) {
            // Tổng
            try (PreparedStatement ps = conn.prepareStatement(totalSql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) stats.put("total", rs.getInt(1));
            }

            // Đang làm
            try (PreparedStatement ps = conn.prepareStatement(workingSql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) stats.put("working", rs.getInt(1));
            }

            // Nghỉ việc
            try (PreparedStatement ps = conn.prepareStatement(inactiveSql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) stats.put("inactive", rs.getInt(1));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stats;
    }

    public boolean addEmployee(Employee emp) {
        String insertSql = """
        INSERT INTO employees (
            first_name, last_name, email, phone, citizen_id,
            date_of_birth, gender, hire_date, employment_status,
            salary_grade, emergency_contact_name, emergency_contact_phone,
            emergency_contact_relationship, department_id, position_id
        )
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertSql)) {

            ps.setString(1, emp.getFirstName());
            ps.setString(2, emp.getLastName());
            ps.setString(3, emp.getEmail());
            ps.setString(4, emp.getPhone());
            ps.setString(5, emp.getCitizenId());
            ps.setDate(6, java.sql.Date.valueOf(emp.getDateOfBirth()));
            ps.setString(7, emp.getGender());
            ps.setDate(8, java.sql.Date.valueOf(emp.getHireDate()));
            ps.setString(9, emp.getEmploymentStatus());
            ps.setFloat(10, emp.getSalaryGrade());
            ps.setString(11, emp.getEmergencyContactName());
            ps.setString(12, emp.getEmergencyContactPhone());
            ps.setString(13, emp.getEmergencyContactRelationship());
            ps.setInt(14, emp.getDepartmentId()); // ✅ dùng id truyền từ controller
            ps.setInt(15, emp.getPositionId());   // ✅ dùng id truyền từ controller

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLIntegrityConstraintViolationException ex) {
            System.err.println("❌ Lỗi trùng email hoặc CCCD: " + ex.getMessage());
        } catch (SQLException e) {
            System.err.println("❌ Lỗi SQL khi thêm nhân viên: " + e.getMessage());
        }

        return false;
    }


    public void deleteEmployee(int id) {
        String sql = "DELETE FROM employees WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace(); // Hoặc log lỗi nếu muốn
        }
    }

    public boolean updateEmployee(Employee employee) {
        String sql = "UPDATE employees SET first_name=?, last_name=?, email=?, phone=?, citizen_id=?, date_of_birth=?, gender=?, department_id=?, position_id=?, hire_date=?, employment_status=?, salary_grade=?, emergency_contact_name=?, emergency_contact_phone=?, emergency_contact_relationship=?, notes=? WHERE id=?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, employee.getFirstName());
            stmt.setString(2, employee.getLastName());
            stmt.setString(3, employee.getEmail());
            stmt.setString(4, employee.getPhone());
            stmt.setString(5, employee.getCitizenId());
            stmt.setDate(6, Date.valueOf(employee.getDateOfBirth()));
            stmt.setString(7, employee.getGender());
            stmt.setInt(8, employee.getDepartmentId());
            stmt.setInt(9, employee.getPositionId());
            stmt.setDate(10, Date.valueOf(employee.getHireDate()));
            stmt.setString(11, employee.getEmploymentStatus());
            stmt.setFloat(12, employee.getSalaryGrade());
            stmt.setString(13, employee.getEmergencyContactName());
            stmt.setString(14, employee.getEmergencyContactPhone());
            stmt.setString(15, employee.getEmergencyContactRelationship());
            stmt.setString(16, employee.getNotes());
            stmt.setInt(17, employee.getId());

            int affected = stmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
