# Hướng dẫn cấu hình quyền cho tài khoản admin@techcorp.com

## 🎯 Mục tiêu
Cấu hình để tài khoản `admin@techcorp.com` có thể truy cập được tab **"Hợp đồng"** và **"Hướng dẫn"**.

## 📋 Tình trạng hiện tại

### Tài khoản admin@techcorp.com:
- **Username**: admin@techcorp.com
- **Password**: admin@techcorp.com
- **Role**: ADMIN (role_id = 1)
- **Quyền hiện có**: 1-12 (Nhân viên, Tổ chức, Cài đặt)
- **Quyền thiếu**: 13-19 (Hợp đồng, Báo cáo/Hướng dẫn)

## 🔧 Các bước thực hiện

### Bước 1: Chạy script SQL để thêm quyền

1. **Mở phpMyAdmin** hoặc MySQL client
2. **Chọn database** `quanlynhansu`
3. **Chạy script** `fix_admin_permissions.sql`:

```sql
-- Script để thêm quyền truy cập Hợp đồng và Hướng dẫn cho ADMIN
-- Chạy script này để admin@techcorp.com có thể truy cập được tab Hợp đồng và Hướng dẫn

-- 1. Thêm các quyền mới cho Hợp đồng và Báo cáo (nếu chưa có)
INSERT IGNORE INTO `permissions` (`id`, `permission_name`, `permission_code`, `module`, `description`) VALUES
(13, 'Xem hợp đồng', 'VIEW_CONTRACT', 'Contract', 'Cho phép xem danh sách hợp đồng'),
(14, 'Thêm hợp đồng', 'ADD_CONTRACT', 'Contract', 'Cho phép thêm hợp đồng mới'),
(15, 'Sửa hợp đồng', 'EDIT_CONTRACT', 'Contract', 'Cho phép chỉnh sửa hợp đồng'),
(16, 'Xóa hợp đồng', 'DELETE_CONTRACT', 'Contract', 'Cho phép xóa hợp đồng'),
(17, 'Xem báo cáo', 'VIEW_REPORT', 'Report', 'Cho phép xem các báo cáo'),
(18, 'Tạo báo cáo', 'CREATE_REPORT', 'Report', 'Cho phép tạo báo cáo mới'),
(19, 'Xuất báo cáo', 'EXPORT_REPORT', 'Report', 'Cho phép xuất báo cáo');

-- 2. Gán tất cả quyền cho ADMIN (role_id = 1)
INSERT IGNORE INTO `role_permissions` (`role_id`, `permission_id`) VALUES
(1, 13), (1, 14), (1, 15), (1, 16), (1, 17), (1, 18), (1, 19);
```

### Bước 2: Kiểm tra quyền đã được cấp

Chạy query sau để kiểm tra:

```sql
SELECT 
    u.username,
    r.role_name,
    p.permission_name,
    p.permission_code
FROM users u
JOIN roles r ON u.role_id = r.id
JOIN role_permissions rp ON u.role_id = rp.role_id
JOIN permissions p ON rp.permission_id = p.id
WHERE u.username = 'admin@techcorp.com'
ORDER BY p.permission_code;
```

**Kết quả mong đợi:**
```
admin@techcorp.com | Admin | Xem nhân viên | VIEW_EMPLOYEE
admin@techcorp.com | Admin | Thêm nhân viên | ADD_EMPLOYEE
admin@techcorp.com | Admin | Sửa nhân viên | EDIT_EMPLOYEE
admin@techcorp.com | Admin | Xóa nhân viên | DELETE_EMPLOYEE
admin@techcorp.com | Admin | Xem phòng ban | VIEW_DEPARTMENT
admin@techcorp.com | Admin | Thêm phòng ban | ADD_DEPARTMENT
admin@techcorp.com | Admin | Sửa phòng ban | EDIT_DEPARTMENT
admin@techcorp.com | Admin | Xóa phòng ban | DELETE_DEPARTMENT
admin@techcorp.com | Admin | Quản lý vai trò | MANAGE_ROLE
admin@techcorp.com | Admin | Phân quyền | MANAGE_PERMISSION
admin@techcorp.com | Admin | Xem bảng lương | VIEW_SALARY
admin@techcorp.com | Admin | Cập nhật bảng lương | EDIT_SALARY
admin@techcorp.com | Admin | Xem hợp đồng | VIEW_CONTRACT
admin@techcorp.com | Admin | Thêm hợp đồng | ADD_CONTRACT
admin@techcorp.com | Admin | Sửa hợp đồng | EDIT_CONTRACT
admin@techcorp.com | Admin | Xóa hợp đồng | DELETE_CONTRACT
admin@techcorp.com | Admin | Xem báo cáo | VIEW_REPORT
admin@techcorp.com | Admin | Tạo báo cáo | CREATE_REPORT
admin@techcorp.com | Admin | Xuất báo cáo | EXPORT_REPORT
```

### Bước 3: Test trong ứng dụng

1. **Chạy ứng dụng** JavaFX
2. **Đăng nhập** với tài khoản `admin@techcorp.com`
3. **Kiểm tra** các tab:
   - ✅ **Dashboard**: Có thể truy cập
   - ✅ **Nhân viên**: Có thể truy cập
   - ✅ **Tổ chức**: Có thể truy cập
   - ✅ **Hợp đồng**: Có thể truy cập (mới)
   - ✅ **Hướng dẫn**: Có thể truy cập (mới)
   - ✅ **Cài đặt**: Có thể truy cập

### Bước 4: Test tooltip phân quyền

1. **Chạy file test**: `TestAdminPermissions.java`
2. **Kiểm tra** tooltip khi hover:
   - Tất cả các tab sẽ hiển thị tooltip xanh "Truy cập [Tên trang]"
   - Không có popup đỏ cảnh báo

## 🔍 Kiểm tra chi tiết

### Kiểm tra trong code:

```java
// Trong MainController.checkPermissionForPage()
case "Hợp đồng":
    return permissionManager.hasPermission("VIEW_CONTRACT");
case "Hướng dẫn":
    return permissionManager.hasPermission("VIEW_REPORT");
```

### Kiểm tra trong database:

```sql
-- Kiểm tra quyền cụ thể
SELECT COUNT(*) as has_permission
FROM users u 
JOIN role_permissions rp ON u.role_id = rp.role_id 
JOIN permissions p ON rp.permission_id = p.id 
WHERE u.username = 'admin@techcorp.com' 
AND p.permission_code = 'VIEW_CONTRACT';

-- Kết quả: 1 (có quyền) hoặc 0 (không có quyền)
```

## 🚨 Xử lý lỗi

### Lỗi 1: "Không đủ quyền truy cập" vẫn hiển thị
**Nguyên nhân**: Database chưa được cập nhật
**Giải pháp**: 
1. Kiểm tra script SQL đã chạy thành công
2. Restart ứng dụng JavaFX
3. Clear cache session

### Lỗi 2: Tab vẫn bị vô hiệu hóa
**Nguyên nhân**: Permission code không khớp
**Giải pháp**:
1. Kiểm tra `permission_code` trong database
2. Đảm bảo `VIEW_CONTRACT` và `VIEW_REPORT` tồn tại
3. Kiểm tra `role_permissions` đã được gán

### Lỗi 3: Database connection error
**Nguyên nhân**: Kết nối database lỗi
**Giải pháp**:
1. Kiểm tra `DBUtil.java`
2. Đảm bảo MySQL service đang chạy
3. Kiểm tra thông tin kết nối

## 📊 Kết quả mong đợi

Sau khi thực hiện thành công:

### ✅ Tài khoản admin@techcorp.com sẽ có:
- **Truy cập đầy đủ** tất cả các tab
- **Tooltip xanh** cho tất cả các tab
- **Không có popup cảnh báo** khi hover
- **Có thể click** vào tất cả các tab

### 🔒 Phân quyền theo vai trò:
- **ADMIN**: Tất cả quyền (1-19)
- **HR**: Quyền nhân viên + hợp đồng + báo cáo (1-4, 13-17)
- **DEPT_MANAGER**: Quyền nhân viên + tổ chức + xem hợp đồng + báo cáo (1, 5, 13, 17)

## 📝 Ghi chú

- Script sử dụng `INSERT IGNORE` để tránh lỗi duplicate
- Quyền được gán theo role, không phải theo user cụ thể
- Cần restart ứng dụng sau khi thay đổi database
- Tooltip sẽ tự động cập nhật theo quyền mới 