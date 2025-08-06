/* package com.main;

import com.model.User;
import com.service.UserService;
import com.utils.DBUtil;

import java.sql.Connection;

public class TestAuthentication {
    
    public static void main(String[] args) {
        System.out.println("=== Test Hệ thống Đăng nhập và Phân quyền ===\n");
        
        // Test kết nối database
        testDatabaseConnection();
        
        // Test đăng nhập
        testUserAuthentication();
        
        // Test phân quyền
        testUserPermissions();
    }
    
    private static void testDatabaseConnection() {
        System.out.println("1. Kiểm tra kết nối database...");
        try {
            Connection conn = DBUtil.getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("✓ Kết nối database thành công!");
                conn.close();
            } else {
                System.out.println("✗ Kết nối database thất bại!");
            }
        } catch (Exception e) {
            System.out.println("✗ Lỗi kết nối database: " + e.getMessage());
        }
        System.out.println();
    }
    
    private static void testUserAuthentication() {
        System.out.println("2. Test đăng nhập người dùng...");
        UserService userService = new UserService();
        
        // Test đăng nhập Admin
        System.out.println("Test đăng nhập Admin:");
        User adminUser = userService.authenticateUser("admin@techcorp.com", "admin@techcorp.com");
        if (adminUser != null) {
            System.out.println("✓ Admin đăng nhập thành công!");
            System.out.println("  - ID: " + adminUser.getId());
            System.out.println("  - Username: " + adminUser.getUsername());
            System.out.println("  - Role: " + adminUser.getRoleName());
            System.out.println("  - Role Code: " + adminUser.getRoleCode());
        } else {
            System.out.println("✗ Admin đăng nhập thất bại!");
        }
        
        // Test đăng nhập HR
        System.out.println("\nTest đăng nhập HR:");
        User hrUser = userService.authenticateUser("hr@techcorp.com", "hr@techcorp.com");
        if (hrUser != null) {
            System.out.println("✓ HR đăng nhập thành công!");
            System.out.println("  - ID: " + hrUser.getId());
            System.out.println("  - Username: " + hrUser.getUsername());
            System.out.println("  - Role: " + hrUser.getRoleName());
            System.out.println("  - Role Code: " + hrUser.getRoleCode());
        } else {
            System.out.println("✗ HR đăng nhập thất bại!");
        }
        
        // Test đăng nhập Department Manager
        System.out.println("\nTest đăng nhập Department Manager:");
        User deptUser = userService.authenticateUser("dept@manager.com", "dept@manager.com");
        if (deptUser != null) {
            System.out.println("✓ Department Manager đăng nhập thành công!");
            System.out.println("  - ID: " + deptUser.getId());
            System.out.println("  - Username: " + deptUser.getUsername());
            System.out.println("  - Role: " + deptUser.getRoleName());
            System.out.println("  - Role Code: " + deptUser.getRoleCode());
        } else {
            System.out.println("✗ Department Manager đăng nhập thất bại!");
        }
        
        // Test đăng nhập sai
        System.out.println("\nTest đăng nhập sai:");
        User wrongUser = userService.authenticateUser("wrong@email.com", "wrongpassword");
        if (wrongUser == null) {
            System.out.println("✓ Xác thực sai thông tin đăng nhập hoạt động đúng!");
        } else {
            System.out.println("✗ Lỗi: Cho phép đăng nhập với thông tin sai!");
        }
        System.out.println();
    }
    
    private static void testUserPermissions() {
        System.out.println("3. Test phân quyền người dùng...");
        UserService userService = new UserService();
        
        // Test quyền Admin
        User adminUser = userService.authenticateUser("admin@techcorp.com", "admin@techcorp.com");
        if (adminUser != null) {
            System.out.println("Test quyền Admin:");
            System.out.println("  - VIEW_EMPLOYEE: " + userService.hasPermission(adminUser.getId(), "VIEW_EMPLOYEE"));
            System.out.println("  - ADD_EMPLOYEE: " + userService.hasPermission(adminUser.getId(), "ADD_EMPLOYEE"));
            System.out.println("  - MANAGE_ROLE: " + userService.hasPermission(adminUser.getId(), "MANAGE_ROLE"));
            System.out.println("  - Is Admin: " + userService.isAdmin(adminUser));
        }
        
        // Test quyền HR
        User hrUser = userService.authenticateUser("hr@techcorp.com", "hr@techcorp.com");
        if (hrUser != null) {
            System.out.println("\nTest quyền HR:");
            System.out.println("  - VIEW_EMPLOYEE: " + userService.hasPermission(hrUser.getId(), "VIEW_EMPLOYEE"));
            System.out.println("  - ADD_EMPLOYEE: " + userService.hasPermission(hrUser.getId(), "ADD_EMPLOYEE"));
            System.out.println("  - MANAGE_ROLE: " + userService.hasPermission(hrUser.getId(), "MANAGE_ROLE"));
            System.out.println("  - Is HR: " + userService.isHR(hrUser));
        }
        
        // Test quyền Department Manager
        User deptUser = userService.authenticateUser("dept@manager.com", "dept@manager.com");
        if (deptUser != null) {
            System.out.println("\nTest quyền Department Manager:");
            System.out.println("  - VIEW_EMPLOYEE: " + userService.hasPermission(deptUser.getId(), "VIEW_EMPLOYEE"));
            System.out.println("  - ADD_EMPLOYEE: " + userService.hasPermission(deptUser.getId(), "ADD_EMPLOYEE"));
            System.out.println("  - VIEW_DEPARTMENT: " + userService.hasPermission(deptUser.getId(), "VIEW_DEPARTMENT"));
            System.out.println("  - Is Dept Manager: " + userService.isDeptManager(deptUser));
        }
        
        System.out.println("\n=== Test hoàn thành ===");
    }
} */