-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Aug 04, 2025 at 03:02 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `quanlynhansu`
--

-- --------------------------------------------------------

--
-- Table structure for table `allowance_types`
--

CREATE TABLE `allowance_types` (
  `id` int(11) NOT NULL,
  `allowance_name` varchar(100) NOT NULL,
  `allowance_code` varchar(20) NOT NULL,
  `amount` decimal(15,2) DEFAULT NULL,
  `calculation_type` enum('Fixed','Percentage','Variable') DEFAULT 'Fixed',
  `is_taxable` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `bonuses`
--

CREATE TABLE `bonuses` (
  `id` int(11) NOT NULL,
  `employee_id` int(11) NOT NULL,
  `bonus_type` enum('Performance','Holiday','Achievement','Annual','Other') NOT NULL,
  `amount` decimal(15,2) NOT NULL,
  `bonus_date` date NOT NULL,
  `reason` text DEFAULT NULL,
  `status` enum('Pending','Approved','Paid') DEFAULT 'Pending',
  `approved_by` int(11) DEFAULT NULL,
  `approved_at` timestamp NULL DEFAULT NULL,
  `paid_date` date DEFAULT NULL,
  `created_by` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `candidates`
--

CREATE TABLE `candidates` (
  `id` int(11) NOT NULL,
  `candidate_code` varchar(20) NOT NULL,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `full_name` varchar(100) GENERATED ALWAYS AS (concat(`first_name`,' ',`last_name`)) STORED,
  `email` varchar(100) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `date_of_birth` date DEFAULT NULL,
  `gender` enum('Male','Female','Other') DEFAULT NULL,
  `address` text DEFAULT NULL,
  `education_level` varchar(100) DEFAULT NULL,
  `work_experience` int(11) DEFAULT 0,
  `skills` text DEFAULT NULL,
  `cv_file_url` varchar(255) DEFAULT NULL,
  `cover_letter` text DEFAULT NULL,
  `expected_salary` decimal(15,2) DEFAULT NULL,
  `source` varchar(50) DEFAULT NULL,
  `status` enum('New','Screening','Interview','Offer','Hired','Rejected') DEFAULT 'New',
  `notes` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `contract_notifications`
--

CREATE TABLE `contract_notifications` (
  `id` int(11) NOT NULL,
  `contract_id` int(11) NOT NULL,
  `notification_type` enum('30_days','60_days','90_days') NOT NULL,
  `notification_date` date NOT NULL,
  `is_sent` tinyint(1) DEFAULT 0,
  `sent_at` timestamp NULL DEFAULT NULL,
  `recipient_email` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `contract_types`
--

CREATE TABLE `contract_types` (
  `id` int(11) NOT NULL,
  `type_name` varchar(100) NOT NULL,
  `type_code` varchar(20) NOT NULL,
  `duration_months` int(11) DEFAULT NULL,
  `description` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `contract_types`
--

INSERT INTO `contract_types` (`id`, `type_name`, `type_code`, `duration_months`, `description`) VALUES
(1, 'Hợp đồng không xác định thời hạn', 'INDEFINITE', NULL, 'Hợp đồng lao động không xác định thời hạn'),
(2, 'Hợp đồng xác định thời hạn 1 năm', 'FIXED_12M', 12, 'Hợp đồng lao động có thời hạn 12 tháng'),
(3, 'Hợp đồng xác định thời hạn 2 năm', 'FIXED_24M', 24, 'Hợp đồng lao động có thời hạn 24 tháng'),
(4, 'Hợp đồng thời vụ', 'SEASONAL', 6, 'Hợp đồng lao động thời vụ'),
(5, 'Hợp đồng thử việc', 'PROBATION', 2, 'Hợp đồng thử việc');

-- --------------------------------------------------------

--
-- Table structure for table `departments`
--

CREATE TABLE `departments` (
  `id` int(11) NOT NULL,
  `department_code` varchar(20) NOT NULL,
  `department_name` varchar(100) NOT NULL,
  `parent_id` int(11) DEFAULT NULL,
  `manager_id` int(11) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `address` text DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `departments`
--

INSERT INTO `departments` (`id`, `department_code`, `department_name`, `parent_id`, `manager_id`, `description`, `address`, `phone`, `email`) VALUES
(1, 'HR', 'Phòng Nhân sự', NULL, NULL, 'Quản lý nhân sự, tuyển dụng và đào tạo', 'Tầng 3 - Tòa nhà A', '0281234567', 'hr@techcorp.com'),
(2, 'ACC', 'Phòng Kế toán', NULL, NULL, 'Xử lý tài chính, lương và chi phí', 'Tầng 2 - Tòa nhà A', '0282345678', 'acc@techcorp.com'),
(3, 'DEV', 'Phòng Phát triển phần mềm', NULL, NULL, 'Phát triển hệ thống phần mềm', 'Tầng 4 - Tòa nhà B', '0283456789', 'dev@techcorp.com'),
(4, 'QA', 'Phòng Kiểm thử (QA)', NULL, NULL, 'Đảm bảo chất lượng phần mềm', 'Tầng 4 - Tòa nhà B', '0284567890', 'qa@techcorp.com'),
(5, 'IT', 'Phòng Hạ tầng - IT Support', NULL, NULL, 'Quản lý hệ thống mạng, phần cứng', 'Tầng 5 - Tòa nhà B', '0285678901', 'it@techcorp.com'),
(6, 'PRODUCT', 'Phòng Quản lý Sản phẩm', NULL, NULL, 'Quản lý định hướng sản phẩm', 'Tầng 6 - Tòa nhà B', '0286789012', 'product@techcorp.com');

-- --------------------------------------------------------

--
-- Table structure for table `employees`
--

CREATE TABLE `employees` (
  `id` int(11) NOT NULL,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `full_name` varchar(100) GENERATED ALWAYS AS (concat(`first_name`,' ',`last_name`)) STORED,
  `email` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `citizen_id` varchar(20) DEFAULT NULL,
  `date_of_birth` date DEFAULT NULL,
  `gender` enum('Male','Female','Other') DEFAULT NULL,
  `address` text DEFAULT NULL,
  `avatar_url` varchar(255) DEFAULT NULL,
  `department_id` int(11) DEFAULT NULL,
  `position_id` int(11) DEFAULT NULL,
  `role_id` int(11) DEFAULT NULL,
  `manager_id` int(11) DEFAULT NULL,
  `hire_date` date DEFAULT NULL,
  `employment_status` enum('Active','Inactive','Terminated','On Leave') DEFAULT 'Active',
  `salary_grade` float DEFAULT NULL,
  `emergency_contact_name` varchar(100) DEFAULT NULL,
  `emergency_contact_phone` varchar(20) DEFAULT NULL,
  `emergency_contact_relationship` varchar(50) DEFAULT NULL,
  `notes` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `employees`
--

INSERT INTO `employees` (`id`, `first_name`, `last_name`, `email`, `phone`, `citizen_id`, `date_of_birth`, `gender`, `address`, `avatar_url`, `department_id`, `position_id`, `role_id`, `manager_id`, `hire_date`, `employment_status`, `salary_grade`, `emergency_contact_name`, `emergency_contact_phone`, `emergency_contact_relationship`, `notes`) VALUES
(1, 'Minh', 'Nguyễn', 'minh.nguyen@techcorp.com', '0901000001', '111111111111', '1985-01-10', 'Male', '123 Đường A, Q1', 'https://example.com/av1.jpg', 1, 1, NULL, NULL, '2015-01-01', 'Active', 15, 'Lan Nguyễn', '0911000001', 'Spouse', NULL),
(2, 'Hà', 'Trần', 'ha.tran@techcorp.com', '0901000002', '111111111112', '1986-02-15', 'Female', '124 Đường B, Q2', 'https://example.com/av2.jpg', 2, 4, NULL, NULL, '2016-02-01', 'Active', 14.5, 'Tú Trần', '0911000002', 'Parent', NULL),
(3, 'Tuấn', 'Phạm', 'tuan.pham@techcorp.com', '0901000003', '111111111113', '1984-03-20', 'Male', '125 Đường C, Q3', 'https://example.com/av3.jpg', 3, 6, NULL, NULL, '2017-03-01', 'Active', 16, 'Hồng Phạm', '0911000003', 'Sibling', NULL),
(4, 'Trang', 'Lê', 'trang.le@techcorp.com', '0901000004', '111111111114', '1987-04-25', 'Female', '126 Đường D, Q4', 'https://example.com/av4.jpg', 4, 10, NULL, NULL, '2018-04-01', 'Active', 13.5, 'Nam Lê', '0911000004', 'Spouse', NULL),
(5, 'Dũng', 'Hoàng', 'dung.hoang@techcorp.com', '0901000005', '111111111115', '1983-05-30', 'Male', '127 Đường E, Q5', 'https://example.com/av5.jpg', 5, 12, NULL, NULL, '2019-05-01', 'Active', 15.5, 'Phương Hoàng', '0911000005', 'Friend', NULL),
(6, 'Phương', 'Vũ', 'phuong.vu@techcorp.com', '0901000006', '111111111116', '1982-06-12', 'Female', '128 Đường F, Q6', 'https://example.com/av6.jpg', 6, 14, NULL, NULL, '2020-06-01', 'Active', 16.2, 'Hà Vũ', '0911000006', 'Parent', NULL),
(7, 'Lan', 'Phan', 'lan.phan@techcorp.com', '0911111101', '222222222201', '1990-03-10', 'Female', '201 Đường G', 'https://example.com/hr1.jpg', 1, 2, 4, 1, '2021-01-01', 'Active', 8.5, 'Minh Phan', '0912000001', 'Sibling', NULL),
(8, 'An', 'Đỗ', 'an.do@techcorp.com', '0911111102', '222222222202', '1992-07-12', 'Male', '202 Đường H', 'https://example.com/hr2.jpg', 1, 3, 4, 1, '2022-01-01', 'Active', 9, 'Trang Đỗ', '0912000002', 'Spouse', NULL),
(9, 'Hùng', 'Bùi', 'hung.bui@techcorp.com', '0911111103', '222222222203', '1989-05-15', 'Male', '203 Đường I', 'https://example.com/acc1.jpg', 2, 5, 4, 2, '2021-04-01', 'Active', 9.5, 'Hà Bùi', '0912000003', 'Friend', NULL),
(10, 'Linh', 'Nguyễn', 'linh.nguyen@techcorp.com', '0911111104', '222222222204', '1991-09-21', 'Female', '204 Đường J', 'https://example.com/acc2.jpg', 2, 5, 4, 2, '2021-06-01', 'On Leave', 9.2, 'Tú Nguyễn', '0912000004', 'Parent', NULL),
(11, 'Tuấn', 'Lê', 'tuan.le@techcorp.com', '0911111105', '222222222205', '1993-10-30', 'Male', '205 Đường K', 'https://example.com/dev1.jpg', 3, 7, 4, 3, '2021-07-01', 'Active', 10, 'Dũng Lê', '0912000005', 'Spouse', NULL),
(12, 'Trang', 'Hoàng', 'trang.hoang@techcorp.com', '0911111106', '222222222206', '1994-12-01', 'Female', '206 Đường L', 'https://example.com/dev2.jpg', 3, 9, 4, 3, '2021-08-01', 'Active', 9.8, 'An Hoàng', '0912000006', 'Sibling', NULL),
(13, 'Minh', 'Đặng', 'minh.dang@techcorp.com', '0911111107', '222222222207', '1990-04-25', 'Male', '207 Đường M', 'https://example.com/qa1.jpg', 4, 11, 4, 4, '2020-03-01', 'Active', 8.7, 'Linh Đặng', '0912000007', 'Friend', NULL),
(14, 'Lê', 'Trần', 'le.tran@techcorp.com', '0911111108', '222222222208', '1991-08-20', 'Female', '208 Đường N', 'https://example.com/qa2.jpg', 4, 11, 4, 4, '2020-05-01', 'Active', 8.9, 'Minh Trần', '0912000008', 'Parent', NULL),
(15, 'Hà', 'Phạm', 'ha.pham@techcorp.com', '0911111109', '222222222209', '1988-02-14', 'Female', '209 Đường O', 'https://example.com/it1.jpg', 5, 13, 4, 5, '2020-09-01', 'Inactive', 9.1, 'Tuấn Phạm', '0912000009', 'Spouse', NULL),
(16, 'Dũng', 'Lê', 'dung.le@techcorp.com', '0911111110', '222222222210', '1992-11-11', 'Male', '210 Đường P', 'https://example.com/it2.jpg', 5, 13, 4, 5, '2021-10-01', 'Active', 9.4, 'Hà Lê', '0912000010', 'Parent', NULL),
(17, 'Phương', 'Ngô', 'phuong.ngo@techcorp.com', '0911111111', '222222222211', '1993-06-01', 'Female', '211 Đường Q', 'https://example.com/prod1.jpg', 6, 15, 4, 6, '2021-12-01', 'Active', 10.1, 'Trang Ngô', '0912000011', 'Spouse', NULL),
(18, 'An', 'Võ', 'an.vo@techcorp.com', '0911111112', '222222222212', '1994-07-07', 'Male', '212 Đường R', 'https://example.com/prod2.jpg', 6, 15, 4, 6, '2022-03-01', 'Active', 9.9, 'Hùng Võ', '0912000012', 'Sibling', NULL),
(20, 'Đỗ', 'Đức Trung', 'trung@gmail.com', '1235467890', '123123123123', '2022-07-07', 'Male', NULL, NULL, 2, 5, NULL, NULL, '2025-07-04', 'Active', 10, '', '', '', NULL),
(21, 'Nguyß╗àn', 'V─ân Admin', 'admin@techcorp.com', '0901234567', '123456789012', '1990-01-01', 'Male', '123 ─Éã░ß╗Øng ABC, Quß║¡n 1, TP.HCM', NULL, 1, 1, 1, NULL, '2020-01-01', 'Active', 1, NULL, NULL, NULL, NULL),
(22, 'Trß║ºn', 'Thß╗ï HR', 'hr_manager@techcorp.com', '0901234568', '123456789013', '1985-05-15', 'Female', '456 ─Éã░ß╗Øng XYZ, Quß║¡n 2, TP.HCM', NULL, 1, 1, NULL, NULL, '2019-06-01', 'Active', 1.2, NULL, NULL, NULL, NULL),
(23, 'L├¬', 'V─ân HR', 'hr_staff@techcorp.com', '0901234569', '123456789014', '1992-08-20', 'Male', '789 ─Éã░ß╗Øng DEF, Quß║¡n 3, TP.HCM', NULL, 1, 2, 4, NULL, '2021-03-15', 'Active', 1, NULL, NULL, NULL, NULL),
(24, 'Phß║ím', 'Thß╗ï Manager', 'dept_manager@techcorp.com', '0901234570', '123456789015', '1988-12-10', 'Female', '321 ─Éã░ß╗Øng GHI, Quß║¡n 7, TP.HCM', NULL, 3, 4, 5, NULL, '2020-09-01', 'Active', 1.3, NULL, NULL, NULL, NULL),
(25, 'Ho├áng', 'V─ân Accountant', 'accountant@techcorp.com', '0901234571', '123456789016', '1991-03-25', 'Male', '654 ─Éã░ß╗Øng JKL, Quß║¡n 5, TP.HCM', NULL, 2, 3, NULL, NULL, '2021-01-10', 'Active', 1.1, NULL, NULL, NULL, NULL),
(26, 'V├Á', 'Thß╗ï Employee', 'employee@techcorp.com', '0901234572', '123456789017', '1995-07-08', 'Female', '987 ─Éã░ß╗Øng MNO, Quß║¡n 10, TP.HCM', NULL, 3, 5, NULL, NULL, '2022-02-20', 'Active', 1, NULL, NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `employment_contracts`
--

CREATE TABLE `employment_contracts` (
  `id` int(11) NOT NULL,
  `contract_number` varchar(50) NOT NULL,
  `employee_id` int(11) NOT NULL,
  `contract_type_id` int(11) NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date DEFAULT NULL,
  `salary` decimal(15,2) DEFAULT NULL,
  `allowances` decimal(15,2) DEFAULT 0.00,
  `benefits` text DEFAULT NULL,
  `terms_conditions` text DEFAULT NULL,
  `status` enum('Active','Expired','Terminated','Renewed') DEFAULT 'Active',
  `signed_date` date DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `created_by` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `interviews`
--

CREATE TABLE `interviews` (
  `id` int(11) NOT NULL,
  `application_id` int(11) NOT NULL,
  `interview_round` int(11) DEFAULT 1,
  `interview_type` enum('Phone','Video','In-person','Technical') NOT NULL,
  `scheduled_date` datetime NOT NULL,
  `duration_minutes` int(11) DEFAULT 60,
  `location` varchar(255) DEFAULT NULL,
  `meeting_link` varchar(255) DEFAULT NULL,
  `interviewer_ids` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`interviewer_ids`)),
  `status` enum('Scheduled','Completed','Cancelled','Rescheduled') DEFAULT 'Scheduled',
  `notes` text DEFAULT NULL,
  `created_by` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `interview_evaluations`
--

CREATE TABLE `interview_evaluations` (
  `id` int(11) NOT NULL,
  `interview_id` int(11) NOT NULL,
  `evaluator_id` int(11) NOT NULL,
  `technical_score` int(11) DEFAULT NULL CHECK (`technical_score` >= 1 and `technical_score` <= 10),
  `communication_score` int(11) DEFAULT NULL CHECK (`communication_score` >= 1 and `communication_score` <= 10),
  `attitude_score` int(11) DEFAULT NULL CHECK (`attitude_score` >= 1 and `attitude_score` <= 10),
  `overall_score` decimal(3,1) GENERATED ALWAYS AS ((`technical_score` + `communication_score` + `attitude_score`) / 3) STORED,
  `strengths` text DEFAULT NULL,
  `weaknesses` text DEFAULT NULL,
  `recommendation` enum('Strongly Recommend','Recommend','Neutral','Not Recommend','Strongly Not Recommend') DEFAULT NULL,
  `feedback` text DEFAULT NULL,
  `evaluated_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `job_applications`
--

CREATE TABLE `job_applications` (
  `id` int(11) NOT NULL,
  `application_code` varchar(20) NOT NULL,
  `recruitment_request_id` int(11) NOT NULL,
  `candidate_id` int(11) NOT NULL,
  `application_date` date NOT NULL,
  `status` enum('Applied','Screening','Interview','Offer','Hired','Rejected') DEFAULT 'Applied',
  `notes` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `monthly_salaries`
--

CREATE TABLE `monthly_salaries` (
  `id` int(11) NOT NULL,
  `employee_id` int(11) NOT NULL,
  `salary_month` date NOT NULL,
  `basic_salary` decimal(15,2) NOT NULL,
  `allowances` decimal(15,2) DEFAULT 0.00,
  `overtime_hours` decimal(5,2) DEFAULT 0.00,
  `overtime_amount` decimal(15,2) DEFAULT 0.00,
  `bonus` decimal(15,2) DEFAULT 0.00,
  `deductions` decimal(15,2) DEFAULT 0.00,
  `tax_amount` decimal(15,2) DEFAULT 0.00,
  `insurance_amount` decimal(15,2) DEFAULT 0.00,
  `net_salary` decimal(15,2) NOT NULL,
  `status` enum('Draft','Approved','Paid') DEFAULT 'Draft',
  `payment_date` date DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `calculated_by` int(11) NOT NULL,
  `approved_by` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `permissions`
--

CREATE TABLE `permissions` (
  `id` int(11) NOT NULL,
  `permission_name` varchar(100) NOT NULL,
  `permission_code` varchar(50) NOT NULL,
  `module` varchar(50) NOT NULL,
  `description` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `permissions`
--

INSERT INTO `permissions` (`id`, `permission_name`, `permission_code`, `module`, `description`) VALUES
(1, 'Xem nhân viên', 'VIEW_EMPLOYEE', 'Employee', 'Cho phép xem danh sách nhân viên'),
(2, 'Thêm nhân viên', 'ADD_EMPLOYEE', 'Employee', 'Cho phép thêm nhân viên mới'),
(3, 'Sửa nhân viên', 'EDIT_EMPLOYEE', 'Employee', 'Cho phép chỉnh sửa thông tin nhân viên'),
(4, 'Xóa nhân viên', 'DELETE_EMPLOYEE', 'Employee', 'Cho phép xoá nhân viên'),
(5, 'Xem phòng ban', 'VIEW_DEPARTMENT', 'Department', 'Cho phép xem danh sách phòng ban'),
(6, 'Thêm phòng ban', 'ADD_DEPARTMENT', 'Department', 'Tạo phòng ban mới'),
(7, 'Sửa phòng ban', 'EDIT_DEPARTMENT', 'Department', 'Chỉnh sửa thông tin phòng ban'),
(8, 'Xóa phòng ban', 'DELETE_DEPARTMENT', 'Department', 'Xóa phòng ban'),
(9, 'Quản lý vai trò', 'MANAGE_ROLE', 'Role', 'Tạo, sửa, xóa vai trò người dùng'),
(10, 'Phân quyền', 'MANAGE_PERMISSION', 'Permission', 'Gán quyền cho vai trò'),
(13, 'Xem hợp đồng', 'VIEW_CONTRACT', 'Contract', 'Cho phép xem danh sách hợp đồng'),
(14, 'Thêm hợp đồng', 'ADD_CONTRACT', 'Contract', 'Cho phép thêm mới hợp đồng'),
(15, 'Sửa hợp đồng', 'EDIT_CONTRACT', 'Contract', 'Cho phép chỉnh sửa hợp đồng'),
(16, 'Xóa hợp đồng', 'DELETE_CONTRACT', 'Contract', 'Cho phép xóa hợp đồng'),
(17, 'Xem tài liệu', 'VIEW_DOCUMENT', 'Documents', 'Cho phép xem tài liệu'),
(18, 'Thêm tài liệu', 'ADD_DOCUMENT', 'Documents', 'Cho phép thêm tài liệu'),
(19, 'Sửa tài liệu', 'EDIT_DOCUMENT', 'Documents', 'Cho phép chỉnh sửa tài liệu'),
(20, 'Xóa tài liệu', 'DELETE_DOCUMENT', 'Documents', 'Cho phép xóa tài liệu');

-- --------------------------------------------------------

--
-- Table structure for table `positions`
--

CREATE TABLE `positions` (
  `id` int(11) NOT NULL,
  `position_code` varchar(20) NOT NULL,
  `position_name` varchar(100) NOT NULL,
  `department_id` int(11) NOT NULL,
  `level` int(11) DEFAULT 1,
  `description` text DEFAULT NULL,
  `requirements` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `positions`
--

INSERT INTO `positions` (`id`, `position_code`, `position_name`, `department_id`, `level`, `description`, `requirements`) VALUES
(1, 'HR_MANAGER', 'Trưởng phòng Nhân sự', 1, 4, 'Quản lý nhân sự công ty', '3+ năm kinh nghiệm quản lý'),
(2, 'RECRUITER', 'Chuyên viên Tuyển dụng', 1, 2, 'Tuyển dụng nhân sự cho công ty', 'Kỹ năng phỏng vấn tốt'),
(3, 'HR_GENERALIST', 'Nhân viên Hành chính - Nhân sự', 1, 2, 'Hỗ trợ các nghiệp vụ nhân sự', 'Thành thạo Excel, quản lý hồ sơ'),
(4, 'ACC_MANAGER', 'Trưởng phòng Kế toán', 2, 4, 'Phụ trách tài chính & lương', 'Kế toán trưởng, CPA là lợi thế'),
(5, 'ACCOUNTANT', 'Nhân viên kế toán', 2, 2, 'Ghi nhận chi phí, lập bảng lương', 'Tốt nghiệp kế toán'),
(6, 'DEV_LEAD', 'Trưởng nhóm Phát triển', 3, 4, 'Quản lý team lập trình viên', 'Kinh nghiệm fullstack, leadership'),
(7, 'BACKEND_DEV', 'Lập trình viên Backend', 3, 2, 'Phát triển API, xử lý logic server', 'Thành thạo Java/Spring, RESTful'),
(8, 'FRONTEND_DEV', 'Lập trình viên Frontend', 3, 2, 'Phát triển giao diện Web/App', 'ReactJS/Angular, UI/UX cơ bản'),
(9, 'FULLSTACK_DEV', 'Lập trình viên Fullstack', 3, 3, 'Xử lý toàn bộ frontend/backend', 'Spring Boot + ReactJS/NextJS'),
(10, 'QA_LEAD', 'Trưởng nhóm QA', 4, 3, 'Lên kế hoạch kiểm thử & review chất lượng', 'Automation + Manual QA kinh nghiệm'),
(11, 'QA_ENGINEER', 'Kỹ sư QA', 4, 2, 'Test phần mềm và báo bug', 'Manual/Automation test'),
(12, 'IT_MANAGER', 'Trưởng phòng IT', 5, 4, 'Quản lý hạ tầng công ty', 'Hiểu rõ hệ thống server/network'),
(13, 'IT_SUPPORT', 'Nhân viên IT Support', 5, 2, 'Hỗ trợ thiết bị, xử lý sự cố', 'Kỹ thuật phần cứng, mạng cơ bản'),
(14, 'PRODUCT_MANAGER', 'Quản lý Sản phẩm (PM)', 6, 4, 'Xác định tính năng và roadmap sản phẩm', 'Kinh nghiệm product owner, agile'),
(15, 'BA', 'Chuyên viên Phân tích nghiệp vụ (BA)', 6, 3, 'Làm việc với khách hàng và team dev', 'Giao tiếp tốt, hiểu quy trình phần mềm');

-- --------------------------------------------------------

--
-- Table structure for table `recruitment_requests`
--

CREATE TABLE `recruitment_requests` (
  `id` int(11) NOT NULL,
  `request_code` varchar(20) NOT NULL,
  `department_id` int(11) NOT NULL,
  `position_id` int(11) NOT NULL,
  `quantity_needed` int(11) NOT NULL,
  `priority` enum('Low','Medium','High','Urgent') DEFAULT 'Medium',
  `expected_start_date` date DEFAULT NULL,
  `budget_min` decimal(15,2) DEFAULT NULL,
  `budget_max` decimal(15,2) DEFAULT NULL,
  `job_description` text DEFAULT NULL,
  `requirements` text DEFAULT NULL,
  `benefits` text DEFAULT NULL,
  `status` enum('Draft','Approved','In Progress','Completed','Cancelled') DEFAULT 'Draft',
  `requested_by` int(11) NOT NULL,
  `approved_by` int(11) DEFAULT NULL,
  `approved_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `report_history`
--

CREATE TABLE `report_history` (
  `id` int(11) NOT NULL,
  `template_id` int(11) NOT NULL,
  `generated_by` int(11) NOT NULL,
  `parameters_used` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`parameters_used`)),
  `file_path` varchar(255) DEFAULT NULL,
  `file_name` varchar(255) DEFAULT NULL,
  `file_size` bigint(20) DEFAULT NULL,
  `generation_time_seconds` decimal(10,2) DEFAULT NULL,
  `status` enum('Success','Failed') DEFAULT 'Success',
  `error_message` text DEFAULT NULL,
  `generated_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `report_templates`
--

CREATE TABLE `report_templates` (
  `id` int(11) NOT NULL,
  `template_name` varchar(100) NOT NULL,
  `template_code` varchar(20) NOT NULL,
  `description` text DEFAULT NULL,
  `sql_query` text DEFAULT NULL,
  `parameters` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`parameters`)),
  `output_format` enum('PDF','Excel','CSV') DEFAULT 'PDF',
  `is_active` tinyint(1) DEFAULT 1,
  `created_by` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `roles`
--

CREATE TABLE `roles` (
  `id` int(11) NOT NULL,
  `role_name` varchar(50) NOT NULL,
  `role_code` varchar(20) NOT NULL,
  `description` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `roles`
--

INSERT INTO `roles` (`id`, `role_name`, `role_code`, `description`) VALUES
(1, 'Admin', 'ADMIN', 'Quản trị toàn bộ hệ thống'),
(4, 'HR Staff', 'HR', 'Chuyên viên nhân sự'),
(5, 'Trưởng phòng ban', 'DEPT_MANAGER', 'Quản lý phòng ban của mình');

-- --------------------------------------------------------

--
-- Table structure for table `role_permissions`
--

CREATE TABLE `role_permissions` (
  `id` int(11) NOT NULL,
  `role_id` int(11) NOT NULL,
  `permission_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `role_permissions`
--

INSERT INTO `role_permissions` (`id`, `role_id`, `permission_id`) VALUES
(1, 1, 1),
(2, 1, 2),
(3, 1, 3),
(4, 1, 4),
(5, 1, 5),
(6, 1, 6),
(7, 1, 7),
(8, 1, 8),
(9, 1, 9),
(10, 1, 10),
(41, 1, 13),
(42, 1, 14),
(43, 1, 15),
(44, 1, 16),
(45, 1, 17),
(46, 1, 18),
(47, 1, 19),
(48, 1, 20),
(33, 4, 1),
(34, 4, 2),
(35, 4, 3),
(36, 4, 4),
(57, 4, 5),
(58, 4, 6),
(59, 4, 7),
(60, 4, 8),
(49, 4, 13),
(50, 4, 14),
(51, 4, 15),
(52, 4, 16),
(53, 4, 17),
(54, 4, 18),
(55, 4, 19),
(56, 4, 20),
(37, 5, 1),
(61, 5, 2),
(62, 5, 3),
(63, 5, 4),
(38, 5, 5),
(64, 5, 6),
(65, 5, 7),
(66, 5, 8),
(67, 5, 17),
(68, 5, 18),
(69, 5, 19),
(70, 5, 20);

-- --------------------------------------------------------

--
-- Table structure for table `salary_allowances`
--

CREATE TABLE `salary_allowances` (
  `id` int(11) NOT NULL,
  `monthly_salary_id` int(11) NOT NULL,
  `allowance_type_id` int(11) NOT NULL,
  `amount` decimal(15,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `salary_scales`
--

CREATE TABLE `salary_scales` (
  `id` int(11) NOT NULL,
  `grade` varchar(10) NOT NULL,
  `level` int(11) NOT NULL,
  `basic_salary` decimal(15,2) NOT NULL,
  `position_allowance` decimal(15,2) DEFAULT 0.00,
  `experience_allowance` decimal(15,2) DEFAULT 0.00,
  `effective_date` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `social_insurance`
--

CREATE TABLE `social_insurance` (
  `id` int(11) NOT NULL,
  `employee_id` int(11) NOT NULL,
  `insurance_number` varchar(20) DEFAULT NULL,
  `start_date` date NOT NULL,
  `end_date` date DEFAULT NULL,
  `employer_contribution_rate` decimal(5,2) DEFAULT 17.50,
  `employee_contribution_rate` decimal(5,2) DEFAULT 8.00,
  `monthly_salary_base` decimal(15,2) DEFAULT NULL,
  `status` enum('Active','Suspended','Terminated') DEFAULT 'Active',
  `notes` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `role_id`) VALUES
(1, 'admin@techcorp.com', 'admin@techcorp.com', 1),
(2, 'hr@techcorp.com', 'hr@techcorp.com', 4),
(3, 'dept@manager.com', 'dept@manager.com', 5);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `allowance_types`
--
ALTER TABLE `allowance_types`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `allowance_code` (`allowance_code`);

--
-- Indexes for table `bonuses`
--
ALTER TABLE `bonuses`
  ADD PRIMARY KEY (`id`),
  ADD KEY `employee_id` (`employee_id`),
  ADD KEY `approved_by` (`approved_by`),
  ADD KEY `created_by` (`created_by`);

--
-- Indexes for table `candidates`
--
ALTER TABLE `candidates`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `candidate_code` (`candidate_code`),
  ADD KEY `idx_candidate_email` (`email`),
  ADD KEY `idx_candidate_status` (`status`);

--
-- Indexes for table `contract_notifications`
--
ALTER TABLE `contract_notifications`
  ADD PRIMARY KEY (`id`),
  ADD KEY `contract_id` (`contract_id`);

--
-- Indexes for table `contract_types`
--
ALTER TABLE `contract_types`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `type_code` (`type_code`);

--
-- Indexes for table `departments`
--
ALTER TABLE `departments`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `department_code` (`department_code`),
  ADD KEY `parent_id` (`parent_id`),
  ADD KEY `manager_id` (`manager_id`);

--
-- Indexes for table `employees`
--
ALTER TABLE `employees`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD UNIQUE KEY `citizen_id` (`citizen_id`),
  ADD KEY `role_id` (`role_id`),
  ADD KEY `manager_id` (`manager_id`),
  ADD KEY `idx_employee_email` (`email`),
  ADD KEY `idx_employee_status` (`employment_status`),
  ADD KEY `idx_employee_department` (`department_id`),
  ADD KEY `idx_employee_position` (`position_id`),
  ADD KEY `idx_employee_role` (`role_id`);

--
-- Indexes for table `employment_contracts`
--
ALTER TABLE `employment_contracts`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `contract_number` (`contract_number`),
  ADD KEY `contract_type_id` (`contract_type_id`),
  ADD KEY `created_by` (`created_by`),
  ADD KEY `idx_contract_employee` (`employee_id`),
  ADD KEY `idx_contract_status` (`status`),
  ADD KEY `idx_contract_end_date` (`end_date`);

--
-- Indexes for table `interviews`
--
ALTER TABLE `interviews`
  ADD PRIMARY KEY (`id`),
  ADD KEY `application_id` (`application_id`),
  ADD KEY `created_by` (`created_by`);

--
-- Indexes for table `interview_evaluations`
--
ALTER TABLE `interview_evaluations`
  ADD PRIMARY KEY (`id`),
  ADD KEY `interview_id` (`interview_id`),
  ADD KEY `evaluator_id` (`evaluator_id`);

--
-- Indexes for table `job_applications`
--
ALTER TABLE `job_applications`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `application_code` (`application_code`),
  ADD KEY `recruitment_request_id` (`recruitment_request_id`),
  ADD KEY `candidate_id` (`candidate_id`),
  ADD KEY `idx_application_status` (`status`),
  ADD KEY `idx_application_date` (`application_date`);

--
-- Indexes for table `monthly_salaries`
--
ALTER TABLE `monthly_salaries`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_employee_month` (`employee_id`,`salary_month`),
  ADD KEY `calculated_by` (`calculated_by`),
  ADD KEY `approved_by` (`approved_by`),
  ADD KEY `idx_salary_employee_month` (`employee_id`,`salary_month`),
  ADD KEY `idx_salary_month` (`salary_month`);

--
-- Indexes for table `permissions`
--
ALTER TABLE `permissions`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `permission_code` (`permission_code`);

--
-- Indexes for table `positions`
--
ALTER TABLE `positions`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `position_code` (`position_code`),
  ADD KEY `department_id` (`department_id`);

--
-- Indexes for table `recruitment_requests`
--
ALTER TABLE `recruitment_requests`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `request_code` (`request_code`),
  ADD KEY `department_id` (`department_id`),
  ADD KEY `position_id` (`position_id`),
  ADD KEY `requested_by` (`requested_by`),
  ADD KEY `approved_by` (`approved_by`);

--
-- Indexes for table `report_history`
--
ALTER TABLE `report_history`
  ADD PRIMARY KEY (`id`),
  ADD KEY `template_id` (`template_id`),
  ADD KEY `generated_by` (`generated_by`);

--
-- Indexes for table `report_templates`
--
ALTER TABLE `report_templates`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `template_code` (`template_code`),
  ADD KEY `created_by` (`created_by`);

--
-- Indexes for table `roles`
--
ALTER TABLE `roles`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `role_name` (`role_name`),
  ADD UNIQUE KEY `role_code` (`role_code`);

--
-- Indexes for table `role_permissions`
--
ALTER TABLE `role_permissions`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_role_permission` (`role_id`,`permission_id`),
  ADD KEY `permission_id` (`permission_id`);

--
-- Indexes for table `salary_allowances`
--
ALTER TABLE `salary_allowances`
  ADD PRIMARY KEY (`id`),
  ADD KEY `monthly_salary_id` (`monthly_salary_id`),
  ADD KEY `allowance_type_id` (`allowance_type_id`);

--
-- Indexes for table `salary_scales`
--
ALTER TABLE `salary_scales`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `grade` (`grade`);

--
-- Indexes for table `social_insurance`
--
ALTER TABLE `social_insurance`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `insurance_number` (`insurance_number`),
  ADD KEY `employee_id` (`employee_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD KEY `role_id` (`role_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `allowance_types`
--
ALTER TABLE `allowance_types`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `bonuses`
--
ALTER TABLE `bonuses`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `candidates`
--
ALTER TABLE `candidates`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `contract_notifications`
--
ALTER TABLE `contract_notifications`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `contract_types`
--
ALTER TABLE `contract_types`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `departments`
--
ALTER TABLE `departments`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `employees`
--
ALTER TABLE `employees`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=27;

--
-- AUTO_INCREMENT for table `employment_contracts`
--
ALTER TABLE `employment_contracts`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `interviews`
--
ALTER TABLE `interviews`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `interview_evaluations`
--
ALTER TABLE `interview_evaluations`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `job_applications`
--
ALTER TABLE `job_applications`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `monthly_salaries`
--
ALTER TABLE `monthly_salaries`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `permissions`
--
ALTER TABLE `permissions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT for table `positions`
--
ALTER TABLE `positions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT for table `recruitment_requests`
--
ALTER TABLE `recruitment_requests`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `report_history`
--
ALTER TABLE `report_history`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `report_templates`
--
ALTER TABLE `report_templates`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `roles`
--
ALTER TABLE `roles`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `role_permissions`
--
ALTER TABLE `role_permissions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=71;

--
-- AUTO_INCREMENT for table `salary_allowances`
--
ALTER TABLE `salary_allowances`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `salary_scales`
--
ALTER TABLE `salary_scales`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `social_insurance`
--
ALTER TABLE `social_insurance`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `bonuses`
--
ALTER TABLE `bonuses`
  ADD CONSTRAINT `bonuses_ibfk_1` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `bonuses_ibfk_2` FOREIGN KEY (`approved_by`) REFERENCES `employees` (`id`),
  ADD CONSTRAINT `bonuses_ibfk_3` FOREIGN KEY (`created_by`) REFERENCES `employees` (`id`);

--
-- Constraints for table `contract_notifications`
--
ALTER TABLE `contract_notifications`
  ADD CONSTRAINT `contract_notifications_ibfk_1` FOREIGN KEY (`contract_id`) REFERENCES `employment_contracts` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `departments`
--
ALTER TABLE `departments`
  ADD CONSTRAINT `departments_ibfk_1` FOREIGN KEY (`parent_id`) REFERENCES `departments` (`id`) ON DELETE SET NULL,
  ADD CONSTRAINT `departments_ibfk_2` FOREIGN KEY (`manager_id`) REFERENCES `employees` (`id`) ON DELETE SET NULL;

--
-- Constraints for table `employees`
--
ALTER TABLE `employees`
  ADD CONSTRAINT `employees_ibfk_1` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE SET NULL,
  ADD CONSTRAINT `employees_ibfk_2` FOREIGN KEY (`position_id`) REFERENCES `positions` (`id`) ON DELETE SET NULL,
  ADD CONSTRAINT `employees_ibfk_3` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE SET NULL,
  ADD CONSTRAINT `employees_ibfk_4` FOREIGN KEY (`manager_id`) REFERENCES `employees` (`id`) ON DELETE SET NULL;

--
-- Constraints for table `employment_contracts`
--
ALTER TABLE `employment_contracts`
  ADD CONSTRAINT `employment_contracts_ibfk_1` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `employment_contracts_ibfk_2` FOREIGN KEY (`contract_type_id`) REFERENCES `contract_types` (`id`),
  ADD CONSTRAINT `employment_contracts_ibfk_3` FOREIGN KEY (`created_by`) REFERENCES `employees` (`id`);

--
-- Constraints for table `interviews`
--
ALTER TABLE `interviews`
  ADD CONSTRAINT `interviews_ibfk_1` FOREIGN KEY (`application_id`) REFERENCES `job_applications` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `interviews_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `employees` (`id`);

--
-- Constraints for table `interview_evaluations`
--
ALTER TABLE `interview_evaluations`
  ADD CONSTRAINT `interview_evaluations_ibfk_1` FOREIGN KEY (`interview_id`) REFERENCES `interviews` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `interview_evaluations_ibfk_2` FOREIGN KEY (`evaluator_id`) REFERENCES `employees` (`id`);

--
-- Constraints for table `job_applications`
--
ALTER TABLE `job_applications`
  ADD CONSTRAINT `job_applications_ibfk_1` FOREIGN KEY (`recruitment_request_id`) REFERENCES `recruitment_requests` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `job_applications_ibfk_2` FOREIGN KEY (`candidate_id`) REFERENCES `candidates` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `monthly_salaries`
--
ALTER TABLE `monthly_salaries`
  ADD CONSTRAINT `monthly_salaries_ibfk_1` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `monthly_salaries_ibfk_2` FOREIGN KEY (`calculated_by`) REFERENCES `employees` (`id`),
  ADD CONSTRAINT `monthly_salaries_ibfk_3` FOREIGN KEY (`approved_by`) REFERENCES `employees` (`id`);

--
-- Constraints for table `positions`
--
ALTER TABLE `positions`
  ADD CONSTRAINT `positions_ibfk_1` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `recruitment_requests`
--
ALTER TABLE `recruitment_requests`
  ADD CONSTRAINT `recruitment_requests_ibfk_1` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`),
  ADD CONSTRAINT `recruitment_requests_ibfk_2` FOREIGN KEY (`position_id`) REFERENCES `positions` (`id`),
  ADD CONSTRAINT `recruitment_requests_ibfk_3` FOREIGN KEY (`requested_by`) REFERENCES `employees` (`id`),
  ADD CONSTRAINT `recruitment_requests_ibfk_4` FOREIGN KEY (`approved_by`) REFERENCES `employees` (`id`);

--
-- Constraints for table `report_history`
--
ALTER TABLE `report_history`
  ADD CONSTRAINT `report_history_ibfk_1` FOREIGN KEY (`template_id`) REFERENCES `report_templates` (`id`),
  ADD CONSTRAINT `report_history_ibfk_2` FOREIGN KEY (`generated_by`) REFERENCES `employees` (`id`);

--
-- Constraints for table `report_templates`
--
ALTER TABLE `report_templates`
  ADD CONSTRAINT `report_templates_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `employees` (`id`);

--
-- Constraints for table `role_permissions`
--
ALTER TABLE `role_permissions`
  ADD CONSTRAINT `role_permissions_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `role_permissions_ibfk_2` FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `salary_allowances`
--
ALTER TABLE `salary_allowances`
  ADD CONSTRAINT `salary_allowances_ibfk_1` FOREIGN KEY (`monthly_salary_id`) REFERENCES `monthly_salaries` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `salary_allowances_ibfk_2` FOREIGN KEY (`allowance_type_id`) REFERENCES `allowance_types` (`id`);

--
-- Constraints for table `social_insurance`
--
ALTER TABLE `social_insurance`
  ADD CONSTRAINT `social_insurance_ibfk_1` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `users`
--
ALTER TABLE `users`
  ADD CONSTRAINT `users_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
