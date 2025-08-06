# Hướng dẫn sử dụng tính năng Tooltip phân quyền

## Tổng quan
Tính năng này cho phép hiển thị popup thông báo "Không đủ quyền truy cập" khi người dùng hover chuột vào các tab mà họ không có quyền truy cập.

## Cách hoạt động

### 1. Khi hover chuột vào tab không có quyền:
- Hiển thị popup màu đỏ với icon cảnh báo ⚠
- Thông báo "Không đủ quyền truy cập"
- Tự động ẩn sau 3 giây
- Có hiệu ứng fade in/out mượt mà

### 2. Khi hover chuột vào tab có quyền:
- Hiển thị tooltip thông thường màu xanh
- Thông báo "Truy cập [Tên trang]"

### 3. Khi click vào tab không có quyền:
- Hiển thị alert dialog cảnh báo
- Ngăn chặn việc chuyển trang

## Phân quyền theo vai trò

### 🔴 ADMIN (Quản trị viên)
- **Có thể truy cập**: Tất cả các trang
- **Tooltip hiển thị**: "Truy cập [Tên trang]"

### 🟡 HR STAFF (Chuyên viên nhân sự)
- **Có thể truy cập**: Dashboard, Nhân viên, Hợp đồng, Báo cáo
- **Không thể truy cập**: Tổ chức, Cài đặt
- **Tooltip cho tab bị cấm**: "Không đủ quyền truy cập"

### 🟢 DEPT_MANAGER (Trưởng phòng ban)
- **Có thể truy cập**: Dashboard, Nhân viên, Tổ chức, Hợp đồng, Báo cáo
- **Không thể truy cập**: Cài đặt
- **Tooltip cho tab bị cấm**: "Không đủ quyền truy cập"

## Cài đặt và sử dụng

### 1. Chạy script SQL để thêm quyền mới:
```sql
-- Chạy file add_permissions.sql để thêm các quyền mới
```

### 2. Tính năng đã được tích hợp tự động:
- Khi khởi động ứng dụng, `MainController.initialize()` sẽ gọi `setupPermissionTooltips()`
- Tất cả các button trong sidebar sẽ được thiết lập tooltip tự động

### 3. Test tính năng:
```java
// Chạy file TestPermissionTooltip.java để test
public class TestPermissionTooltip extends Application {
    // Demo với user HR
}
```

## Cấu trúc code

### PermissionTooltipManager.java
- Quản lý việc hiển thị tooltip và popup
- Singleton pattern để đảm bảo chỉ có một instance
- Xử lý animation và timing

### MainController.java
- Tích hợp kiểm tra quyền khi click button
- Hiển thị alert khi không đủ quyền
- Gọi setupPermissionTooltips() trong initialize()

### sidebar.css
- Style cho button bị vô hiệu hóa (.disabled-button)
- Màu sắc và hiệu ứng hover

## Tùy chỉnh

### Thay đổi thời gian hiển thị popup:
```java
// Trong PermissionTooltipManager.java, dòng 95
PauseTransition pause = new PauseTransition(Duration.seconds(3)); // Thay đổi số giây
```

### Thay đổi màu sắc popup:
```java
// Trong PermissionTooltipManager.java, dòng 67
content.setStyle("-fx-background-color: #e74c3c; ..."); // Thay đổi màu nền
```

### Thêm quyền mới:
1. Thêm vào bảng `permissions`
2. Gán cho các role trong `role_permissions`
3. Cập nhật `checkPermissionForPage()` trong MainController

## Lưu ý
- Tính năng này hoạt động dựa trên session hiện tại của user
- Cần đảm bảo SessionManager đã được khởi tạo đúng cách
- Popup sẽ tự động ẩn khi di chuyển chuột ra khỏi button 