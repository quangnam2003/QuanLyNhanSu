-- =============================================
-- HỆ THỐNG QUẢN LÝ NHÂN SỰ - DATABASE SCHEMA
-- =============================================

-- 1. BẢNG HỆ THỐNG VÀ PHÂN QUYỀN
-- =============================================

-- Bảng vai trò hệ thống

CREATE DATABASE QuanLyNhanSu;
USE QuanLyNhanSu;
CREATE TABLE roles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    role_code VARCHAR(20) NOT NULL UNIQUE,
    description TEXT
);

-- Bảng quyền hệ thống
CREATE TABLE permissions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    permission_name VARCHAR(100) NOT NULL,
    permission_code VARCHAR(50) NOT NULL UNIQUE,
    module VARCHAR(50) NOT NULL,
    description TEXT
);

-- Bảng phân quyền vai trò
CREATE TABLE role_permissions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    role_id INT NOT NULL,
    permission_id INT NOT NULL,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE,
    UNIQUE KEY unique_role_permission (role_id, permission_id)
);

-- 2. BẢNG TỔ CHỨC VÀ CẤU TRÚC
-- =============================================

-- Bảng phòng ban
CREATE TABLE departments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    department_code VARCHAR(20) NOT NULL UNIQUE,
    department_name VARCHAR(100) NOT NULL,
    parent_id INT NULL,
    manager_id INT NULL,
    description TEXT,
    address TEXT,
    phone VARCHAR(20),
    email VARCHAR(100),
    FOREIGN KEY (parent_id) REFERENCES departments(id) ON DELETE SET NULL
);

-- Bảng chức vụ/vị trí
CREATE TABLE positions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    position_code VARCHAR(20) NOT NULL UNIQUE,
    position_name VARCHAR(100) NOT NULL,
    department_id INT NOT NULL,
    level INT DEFAULT 1,
    description TEXT,
    requirements TEXT,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE CASCADE
);

-- 3. BẢNG NHÂN VIÊN
-- =============================================

-- Bảng nhân viên chính
CREATE TABLE employees (
    id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    full_name VARCHAR(100) GENERATED ALWAYS AS (CONCAT(first_name, ' ', last_name)) STORED,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    citizen_id VARCHAR(20) UNIQUE,
    date_of_birth DATE,
    gender ENUM('Male', 'Female', 'Other'),
    address TEXT,
    avatar_url VARCHAR(255),
    department_id INT,
    position_id INT,
    role_id INT,
    manager_id INT,
    hire_date DATE,
    employment_status ENUM('Active', 'Inactive', 'Terminated', 'On Leave') DEFAULT 'Active',
    salary_grade FLOAT,
    emergency_contact_name VARCHAR(100),
    emergency_contact_phone VARCHAR(20),
    emergency_contact_relationship VARCHAR(50),
    notes TEXT,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL,
    FOREIGN KEY (position_id) REFERENCES positions(id) ON DELETE SET NULL,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE SET NULL,
    FOREIGN KEY (manager_id) REFERENCES employees(id) ON DELETE SET NULL
);




-- Thêm foreign key cho manager của phòng ban
ALTER TABLE departments ADD FOREIGN KEY (manager_id) REFERENCES employees(id) ON DELETE SET NULL;


-- 4. BẢNG HỢP ĐỒNG LAO ĐỘNG
-- =============================================

-- Bảng loại hợp đồng
CREATE TABLE contract_types (
    id INT PRIMARY KEY AUTO_INCREMENT,
    type_name VARCHAR(100) NOT NULL,
    type_code VARCHAR(20) NOT NULL UNIQUE,
    duration_months INT,
    description TEXT
);

-- Bảng hợp đồng lao động
CREATE TABLE employment_contracts (
    id INT PRIMARY KEY AUTO_INCREMENT,
    contract_number VARCHAR(50) NOT NULL UNIQUE,
    employee_id INT NOT NULL,
    contract_type_id INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    salary DECIMAL(15,2),
    allowances DECIMAL(15,2) DEFAULT 0,
    benefits TEXT,
    terms_conditions TEXT,
    status ENUM('Active', 'Expired', 'Terminated', 'Renewed') DEFAULT 'Active',
    signed_date DATE,
    notes TEXT,
    created_by INT NOT NULL,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    FOREIGN KEY (contract_type_id) REFERENCES contract_types(id),
    FOREIGN KEY (created_by) REFERENCES employees(id)
);

-- Bảng thông báo hết hạn hợp đồng
CREATE TABLE contract_notifications (
    id INT PRIMARY KEY AUTO_INCREMENT,
    contract_id INT NOT NULL,
    notification_type ENUM('30_days', '60_days', '90_days') NOT NULL,
    notification_date DATE NOT NULL,
    is_sent BOOLEAN DEFAULT FALSE,
    sent_at TIMESTAMP NULL,
    recipient_email VARCHAR(100),
    FOREIGN KEY (contract_id) REFERENCES employment_contracts(id) ON DELETE CASCADE
);

-- 5. BẢNG TUYỂN DỤNG
-- =============================================

-- Bảng nhu cầu tuyển dụng
CREATE TABLE recruitment_requests (
    id INT PRIMARY KEY AUTO_INCREMENT,
    request_code VARCHAR(20) NOT NULL UNIQUE,
    department_id INT NOT NULL,
    position_id INT NOT NULL,
    quantity_needed INT NOT NULL,
    priority ENUM('Low', 'Medium', 'High', 'Urgent') DEFAULT 'Medium',
    expected_start_date DATE,
    budget_min DECIMAL(15,2),
    budget_max DECIMAL(15,2),
    job_description TEXT,
    requirements TEXT,
    benefits TEXT,
    status ENUM('Draft', 'Approved', 'In Progress', 'Completed', 'Cancelled') DEFAULT 'Draft',
    requested_by INT NOT NULL,
    approved_by INT,
    approved_at TIMESTAMP NULL,
    FOREIGN KEY (department_id) REFERENCES departments(id),
    FOREIGN KEY (position_id) REFERENCES positions(id),
    FOREIGN KEY (requested_by) REFERENCES employees(id),
    FOREIGN KEY (approved_by) REFERENCES employees(id)
);

-- Bảng ứng viên
CREATE TABLE candidates (
    id INT PRIMARY KEY AUTO_INCREMENT,
    candidate_code VARCHAR(20) NOT NULL UNIQUE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    full_name VARCHAR(100) GENERATED ALWAYS AS (CONCAT(first_name, ' ', last_name)) STORED,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    date_of_birth DATE,
    gender ENUM('Male', 'Female', 'Other'),
    address TEXT,
    education_level VARCHAR(100),
    work_experience INT DEFAULT 0,
    skills TEXT,
    cv_file_url VARCHAR(255),
    cover_letter TEXT,
    expected_salary DECIMAL(15,2),
    source VARCHAR(50),
    status ENUM('New', 'Screening', 'Interview', 'Offer', 'Hired', 'Rejected') DEFAULT 'New',
    notes TEXT
);

-- Bảng ứng tuyển
CREATE TABLE job_applications (
    id INT PRIMARY KEY AUTO_INCREMENT,
    application_code VARCHAR(20) NOT NULL UNIQUE,
    recruitment_request_id INT NOT NULL,
    candidate_id INT NOT NULL,
    application_date DATE NOT NULL,
    status ENUM('Applied', 'Screening', 'Interview', 'Offer', 'Hired', 'Rejected') DEFAULT 'Applied',
    notes TEXT,
    FOREIGN KEY (recruitment_request_id) REFERENCES recruitment_requests(id) ON DELETE CASCADE,
    FOREIGN KEY (candidate_id) REFERENCES candidates(id) ON DELETE CASCADE
);

-- Bảng lịch phỏng vấn
CREATE TABLE interviews (
    id INT PRIMARY KEY AUTO_INCREMENT,
    application_id INT NOT NULL,
    interview_round INT DEFAULT 1,
    interview_type ENUM('Phone', 'Video', 'In-person', 'Technical') NOT NULL,
    scheduled_date DATETIME NOT NULL,
    duration_minutes INT DEFAULT 60,
    location VARCHAR(255),
    meeting_link VARCHAR(255),
    interviewer_ids JSON,
    status ENUM('Scheduled', 'Completed', 'Cancelled', 'Rescheduled') DEFAULT 'Scheduled',
    notes TEXT,
    created_by INT NOT NULL,
    FOREIGN KEY (application_id) REFERENCES job_applications(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES employees(id)
);

-- Bảng đánh giá phỏng vấn
CREATE TABLE interview_evaluations (
    id INT PRIMARY KEY AUTO_INCREMENT,
    interview_id INT NOT NULL,
    evaluator_id INT NOT NULL,
    technical_score INT CHECK (technical_score >= 1 AND technical_score <= 10),
    communication_score INT CHECK (communication_score >= 1 AND communication_score <= 10),
    attitude_score INT CHECK (attitude_score >= 1 AND attitude_score <= 10),
    overall_score DECIMAL(3,1) GENERATED ALWAYS AS ((technical_score + communication_score + attitude_score) / 3) STORED,
    strengths TEXT,
    weaknesses TEXT,
    recommendation ENUM('Strongly Recommend', 'Recommend', 'Neutral', 'Not Recommend', 'Strongly Not Recommend'),
    feedback TEXT,
    evaluated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (interview_id) REFERENCES interviews(id) ON DELETE CASCADE,
    FOREIGN KEY (evaluator_id) REFERENCES employees(id)
);

-- 6. BẢNG LƯƠNG VÀ PHÚC LỢI
-- =============================================

-- Bảng bảng lương cơ bản
CREATE TABLE salary_scales (
    id INT PRIMARY KEY AUTO_INCREMENT,
    grade VARCHAR(10) NOT NULL UNIQUE,
    level INT NOT NULL,
    basic_salary DECIMAL(15,2) NOT NULL,
    position_allowance DECIMAL(15,2) DEFAULT 0,
    experience_allowance DECIMAL(15,2) DEFAULT 0,
    effective_date DATE NOT NULL
);

-- Bảng phụ cấp
CREATE TABLE allowance_types (
    id INT PRIMARY KEY AUTO_INCREMENT,
    allowance_name VARCHAR(100) NOT NULL,
    allowance_code VARCHAR(20) NOT NULL UNIQUE,
    amount DECIMAL(15,2),
    calculation_type ENUM('Fixed', 'Percentage', 'Variable') DEFAULT 'Fixed',
    is_taxable BOOLEAN DEFAULT TRUE
);

-- Bảng lương tháng
CREATE TABLE monthly_salaries (
    id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT NOT NULL,
    salary_month DATE NOT NULL,
    basic_salary DECIMAL(15,2) NOT NULL,
    allowances DECIMAL(15,2) DEFAULT 0,
    overtime_hours DECIMAL(5,2) DEFAULT 0,
    overtime_amount DECIMAL(15,2) DEFAULT 0,
    bonus DECIMAL(15,2) DEFAULT 0,
    deductions DECIMAL(15,2) DEFAULT 0,
    tax_amount DECIMAL(15,2) DEFAULT 0,
    insurance_amount DECIMAL(15,2) DEFAULT 0,
    net_salary DECIMAL(15,2) NOT NULL,
    status ENUM('Draft', 'Approved', 'Paid') DEFAULT 'Draft',
    payment_date DATE,
    notes TEXT,
    calculated_by INT NOT NULL,
    approved_by INT,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    FOREIGN KEY (calculated_by) REFERENCES employees(id),
    FOREIGN KEY (approved_by) REFERENCES employees(id),
    UNIQUE KEY unique_employee_month (employee_id, salary_month)
);

-- Bảng chi tiết phụ cấp lương
CREATE TABLE salary_allowances (
    id INT PRIMARY KEY AUTO_INCREMENT,
    monthly_salary_id INT NOT NULL,
    allowance_type_id INT NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    FOREIGN KEY (monthly_salary_id) REFERENCES monthly_salaries(id) ON DELETE CASCADE,
    FOREIGN KEY (allowance_type_id) REFERENCES allowance_types(id)
);

-- Bảng thưởng
CREATE TABLE bonuses (
    id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT NOT NULL,
    bonus_type ENUM('Performance', 'Holiday', 'Achievement', 'Annual', 'Other') NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    bonus_date DATE NOT NULL,
    reason TEXT,
    status ENUM('Pending', 'Approved', 'Paid') DEFAULT 'Pending',
    approved_by INT,
    approved_at TIMESTAMP NULL,
    paid_date DATE,
    created_by INT NOT NULL,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    FOREIGN KEY (approved_by) REFERENCES employees(id),
    FOREIGN KEY (created_by) REFERENCES employees(id)
);

-- Bảng bảo hiểm xã hội
CREATE TABLE social_insurance (
    id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT NOT NULL,
    insurance_number VARCHAR(20) UNIQUE,
    start_date DATE NOT NULL,
    end_date DATE,
    employer_contribution_rate DECIMAL(5,2) DEFAULT 17.5,
    employee_contribution_rate DECIMAL(5,2) DEFAULT 8.0,
    monthly_salary_base DECIMAL(15,2),
    status ENUM('Active', 'Suspended', 'Terminated') DEFAULT 'Active',
    notes TEXT,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
);

-- 7. BẢNG THỐNG KÊ VÀ BÁO CÁO
-- =============================================

-- Bảng mẫu báo cáo
CREATE TABLE report_templates (
    id INT PRIMARY KEY AUTO_INCREMENT,
    template_name VARCHAR(100) NOT NULL,
    template_code VARCHAR(20) NOT NULL UNIQUE,
    description TEXT,
    sql_query TEXT,
    parameters JSON,
    output_format ENUM('PDF', 'Excel', 'CSV') DEFAULT 'PDF',
    is_active BOOLEAN DEFAULT TRUE,
    created_by INT NOT NULL,
    FOREIGN KEY (created_by) REFERENCES employees(id)
);

-- Bảng lịch sử xuất báo cáo
CREATE TABLE report_history (
    id INT PRIMARY KEY AUTO_INCREMENT,
    template_id INT NOT NULL,
    generated_by INT NOT NULL,
    parameters_used JSON,
    file_path VARCHAR(255),
    file_name VARCHAR(255),
    file_size BIGINT,
    generation_time_seconds DECIMAL(10,2),
    status ENUM('Success', 'Failed') DEFAULT 'Success',
    error_message TEXT,
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (template_id) REFERENCES report_templates(id),
    FOREIGN KEY (generated_by) REFERENCES employees(id)
);



-- =============================================
-- DỮ LIỆU MẪU (SAMPLE DATA)
-- =============================================

-- Thêm vai trò mặc định
INSERT INTO roles (role_name, role_code, description) VALUES
('System Admin', 'ADMIN', 'Quản trị viên hệ thống - quyền cao nhất'),
('HR Director', 'HR_DIRECTOR', 'Giám đốc nhân sự - quản lý toàn bộ'),
('HR Manager', 'HR_MANAGER', 'Trưởng phòng nhân sự'),
('HR Staff', 'HR_STAFF', 'Nhân viên phòng nhân sự'),
('Department Manager', 'DEPT_MANAGER', 'Trưởng phòng ban'),
('Employee', 'EMPLOYEE', 'Nhân viên thường');

-- Thêm loại hợp đồng mặc định
INSERT INTO contract_types (type_name, type_code, duration_months, description) VALUES
('Hợp đồng không xác định thời hạn', 'INDEFINITE', NULL, 'Hợp đồng lao động không xác định thời hạn'),
('Hợp đồng xác định thời hạn 1 năm', 'FIXED_12M', 12, 'Hợp đồng lao động có thời hạn 12 tháng'),
('Hợp đồng xác định thời hạn 2 năm', 'FIXED_24M', 24, 'Hợp đồng lao động có thời hạn 24 tháng'),
('Hợp đồng thời vụ', 'SEASONAL', 6, 'Hợp đồng lao động thời vụ'),
('Hợp đồng thử việc', 'PROBATION', 2, 'Hợp đồng thử việc');



-- =============================================
-- INDEXES ĐỂ TỐI ƯU HIỆU SUẤT
-- =============================================

-- Indexes cho bảng employees
CREATE INDEX idx_employee_code ON employees(employee_code);
CREATE INDEX idx_employee_email ON employees(email);
CREATE INDEX idx_employee_status ON employees(employment_status);
CREATE INDEX idx_employee_department ON employees(department_id);
CREATE INDEX idx_employee_position ON employees(position_id);

-- Indexes cho bảng contracts
CREATE INDEX idx_contract_employee ON employment_contracts(employee_id);
CREATE INDEX idx_contract_status ON employment_contracts(status);
CREATE INDEX idx_contract_end_date ON employment_contracts(end_date);

-- Indexes cho bảng salary
CREATE INDEX idx_salary_employee_month ON monthly_salaries(employee_id, salary_month);
CREATE INDEX idx_salary_month ON monthly_salaries(salary_month);

-- Indexes cho bảng candidates
CREATE INDEX idx_candidate_email ON candidates(email);
CREATE INDEX idx_candidate_status ON candidates(status);

-- Indexes cho bảng applications
CREATE INDEX idx_application_status ON job_applications(status);
CREATE INDEX idx_application_date ON job_applications(application_date);

ALTER TABLE employees AUTO_INCREMENT = 1;

INSERT INTO roles (role_name, role_code, description) VALUES
('Admin', 'ADMIN', 'Quản trị toàn bộ hệ thống'),
('Giám đốc nhân sự', 'HR_DIRECTOR', 'Giám sát toàn bộ hoạt động nhân sự'),
('Trưởng phòng nhân sự', 'HR_MANAGER', 'Quản lý nhân sự và các HR Staff'),
('HR Staff', 'HR', 'Chuyên viên nhân sự'),
('Trưởng phòng ban', 'DEPT_MANAGER', 'Quản lý phòng ban của mình'),
('Accountant', 'ACC', 'Phụ trách lương và tài chính');

INSERT INTO permissions (permission_name, permission_code, module, description) VALUES
-- Nhân viên
('Xem nhân viên', 'VIEW_EMPLOYEE', 'Employee', 'Cho phép xem danh sách nhân viên'),
('Thêm nhân viên', 'ADD_EMPLOYEE', 'Employee', 'Cho phép thêm nhân viên mới'),
('Sửa nhân viên', 'EDIT_EMPLOYEE', 'Employee', 'Cho phép chỉnh sửa thông tin nhân viên'),
('Xóa nhân viên', 'DELETE_EMPLOYEE', 'Employee', 'Cho phép xoá nhân viên'),

-- Phòng ban
('Xem phòng ban', 'VIEW_DEPARTMENT', 'Department', 'Cho phép xem danh sách phòng ban'),
('Thêm phòng ban', 'ADD_DEPARTMENT', 'Department', 'Tạo phòng ban mới'),
('Sửa phòng ban', 'EDIT_DEPARTMENT', 'Department', 'Chỉnh sửa thông tin phòng ban'),
('Xóa phòng ban', 'DELETE_DEPARTMENT', 'Department', 'Xóa phòng ban'),

-- Vai trò và phân quyền
('Quản lý vai trò', 'MANAGE_ROLE', 'Role', 'Tạo, sửa, xóa vai trò người dùng'),
('Phân quyền', 'MANAGE_PERMISSION', 'Permission', 'Gán quyền cho vai trò'),

-- Lương
('Xem bảng lương', 'VIEW_SALARY', 'Salary', 'Truy cập thông tin bảng lương'),
('Cập nhật bảng lương', 'EDIT_SALARY', 'Salary', 'Cập nhật thông tin bảng lương');

-- Admin (id = 1) có toàn bộ quyền
INSERT INTO role_permissions (role_id, permission_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4),
(1, 5), (1, 6), (1, 7), (1, 8),
(1, 9), (1, 10),
(1, 11), (1, 12);

-- Giám đốc nhân sự (id = 2): tất cả quyền liên quan đến nhân viên, phòng ban, phân quyền, lương
INSERT INTO role_permissions (role_id, permission_id) VALUES
(2, 1), (2, 2), (2, 3), (2, 4),
(2, 5), (2, 6), (2, 7), (2, 8),
(2, 11), (2, 12), (2, 10);

-- Trưởng phòng nhân sự (id = 3): nhân viên + phòng ban + xem bảng lương
INSERT INTO role_permissions (role_id, permission_id) VALUES
(3, 1), (3, 2), (3, 3), (3, 4),
(3, 5), (3, 6), (3, 7), (3, 8),
(3, 11);

-- HR Staff (id = 4): chỉ thao tác nhân viên
INSERT INTO role_permissions (role_id, permission_id) VALUES
(4, 1), (4, 2), (4, 3), (4, 4);

-- Trưởng phòng ban (id = 5): chỉ được xem nhân viên + phòng ban
INSERT INTO role_permissions (role_id, permission_id) VALUES
(5, 1), (5, 5);

-- Accountant (id = 6): chỉ liên quan bảng lương
INSERT INTO role_permissions (role_id, permission_id) VALUES
(6, 11), (6, 12);
INSERT INTO departments (department_code, department_name, parent_id, manager_id, description, address, phone, email) VALUES
('HR', 'Phòng Nhân sự', NULL, NULL, 'Quản lý nhân sự, tuyển dụng và đào tạo', 'Tầng 3 - Tòa nhà A', '0281234567', 'hr@techcorp.com'),
('ACC', 'Phòng Kế toán', NULL, NULL, 'Xử lý tài chính, lương và chi phí', 'Tầng 2 - Tòa nhà A', '0282345678', 'acc@techcorp.com'),
('DEV', 'Phòng Phát triển phần mềm', NULL, NULL, 'Phát triển hệ thống phần mềm', 'Tầng 4 - Tòa nhà B', '0283456789', 'dev@techcorp.com'),
('QA', 'Phòng Kiểm thử (QA)', NULL, NULL, 'Đảm bảo chất lượng phần mềm', 'Tầng 4 - Tòa nhà B', '0284567890', 'qa@techcorp.com'),
('IT', 'Phòng Hạ tầng - IT Support', NULL, NULL, 'Quản lý hệ thống mạng, phần cứng', 'Tầng 5 - Tòa nhà B', '0285678901', 'it@techcorp.com'),
('PRODUCT', 'Phòng Quản lý Sản phẩm', NULL, NULL, 'Quản lý định hướng sản phẩm', 'Tầng 6 - Tòa nhà B', '0286789012', 'product@techcorp.com');
INSERT INTO positions (position_code, position_name, department_id, level, description, requirements) VALUES
-- HR
('HR_MANAGER', 'Trưởng phòng Nhân sự', 1, 4, 'Quản lý nhân sự công ty', '3+ năm kinh nghiệm quản lý'),
('RECRUITER', 'Chuyên viên Tuyển dụng', 1, 2, 'Tuyển dụng nhân sự cho công ty', 'Kỹ năng phỏng vấn tốt'),
('HR_GENERALIST', 'Nhân viên Hành chính - Nhân sự', 1, 2, 'Hỗ trợ các nghiệp vụ nhân sự', 'Thành thạo Excel, quản lý hồ sơ'),

-- Kế toán
('ACC_MANAGER', 'Trưởng phòng Kế toán', 2, 4, 'Phụ trách tài chính & lương', 'Kế toán trưởng, CPA là lợi thế'),
('ACCOUNTANT', 'Nhân viên kế toán', 2, 2, 'Ghi nhận chi phí, lập bảng lương', 'Tốt nghiệp kế toán'),

-- DEV
('DEV_LEAD', 'Trưởng nhóm Phát triển', 3, 4, 'Quản lý team lập trình viên', 'Kinh nghiệm fullstack, leadership'),
('BACKEND_DEV', 'Lập trình viên Backend', 3, 2, 'Phát triển API, xử lý logic server', 'Thành thạo Java/Spring, RESTful'),
('FRONTEND_DEV', 'Lập trình viên Frontend', 3, 2, 'Phát triển giao diện Web/App', 'ReactJS/Angular, UI/UX cơ bản'),
('FULLSTACK_DEV', 'Lập trình viên Fullstack', 3, 3, 'Xử lý toàn bộ frontend/backend', 'Spring Boot + ReactJS/NextJS'),

-- QA
('QA_LEAD', 'Trưởng nhóm QA', 4, 3, 'Lên kế hoạch kiểm thử & review chất lượng', 'Automation + Manual QA kinh nghiệm'),
('QA_ENGINEER', 'Kỹ sư QA', 4, 2, 'Test phần mềm và báo bug', 'Manual/Automation test'),

-- IT SUPPORT
('IT_MANAGER', 'Trưởng phòng IT', 5, 4, 'Quản lý hạ tầng công ty', 'Hiểu rõ hệ thống server/network'),
('IT_SUPPORT', 'Nhân viên IT Support', 5, 2, 'Hỗ trợ thiết bị, xử lý sự cố', 'Kỹ thuật phần cứng, mạng cơ bản'),

-- PRODUCT
('PRODUCT_MANAGER', 'Quản lý Sản phẩm (PM)', 6, 4, 'Xác định tính năng và roadmap sản phẩm', 'Kinh nghiệm product owner, agile'),
('BA', 'Chuyên viên Phân tích nghiệp vụ (BA)', 6, 3, 'Làm việc với khách hàng và team dev', 'Giao tiếp tốt, hiểu quy trình phần mềm');INSERT INTO roles (role_name, role_code, description) VALUES ('Admin', 'ADMIN', 'Quản trị toàn bộ hệ thống'), ('Giám đốc nhân sự', 'HR_DIRECTOR', 'Giám sát toàn bộ hoạt động nhân sự'), ('Trưởng phòng nhân sự', 'HR_MANAGER', 'Quản lý nhân sự và các HR Staff'), ('HR Staff', 'HR', 'Chuyên viên nhân sự'), ('Trưởng phòng ban', 'DEPT_MANAGER', 'Quản lý phòng ban của mình'), ('Accountant', 'ACC', 'Phụ trách lương và tài chính')
INSERT INTO roles (role_name, role_code, description) VALUES ('Admin', 'ADMIN', 'Quản trị toàn bộ hệ thống'), ('Giám đốc nhân sự', 'HR_DIRECTOR', 'Giám sát toàn bộ hoạt động nhân sự'), ('Trưởng phòng nhân sự', 'HR_MANAGER', 'Quản lý nhân sự và các HR Staff'), ('HR Staff', 'HR', 'Chuyên viên nhân sự'), ('Trưởng phòng ban', 'DEPT_MANAGER', 'Quản lý phòng ban của mình'), ('Accountant', 'ACC', 'Phụ trách lương và tài chính')
