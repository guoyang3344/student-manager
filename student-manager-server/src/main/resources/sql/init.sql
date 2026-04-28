-- 创建数据库
CREATE DATABASE IF NOT EXISTS student_manager DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE student_manager;

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL COMMENT '角色编码',
    description VARCHAR(200) COMMENT '角色描述',
    status INT DEFAULT 1 COMMENT '状态：0禁用 1启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
    UNIQUE KEY uk_role_code (role_code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    avatar VARCHAR(255) COMMENT '头像',
    status INT DEFAULT 1 COMMENT '状态：0禁用 1启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
    UNIQUE KEY uk_username (username),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_role (user_id, role_id),
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id),
    FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES sys_role(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 宿舍楼表
CREATE TABLE IF NOT EXISTS dormitory_building (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    building_name VARCHAR(50) NOT NULL COMMENT '宿舍楼名称',
    building_number VARCHAR(20) COMMENT '宿舍楼编号',
    floor_count INT DEFAULT 6 COMMENT '楼层数',
    room_count_per_floor INT DEFAULT 10 COMMENT '每层房间数',
    gender_type INT DEFAULT 1 COMMENT '性别类型：1男 2女 3混合',
    description VARCHAR(200) COMMENT '描述',
    status INT DEFAULT 1 COMMENT '状态：0禁用 1启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
    INDEX idx_building_number (building_number),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宿舍楼表';

-- 宿舍表
CREATE TABLE IF NOT EXISTS dormitory (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    building_id BIGINT NOT NULL COMMENT '宿舍楼ID',
    room_number VARCHAR(20) NOT NULL COMMENT '房间号',
    floor INT COMMENT '楼层',
    bed_count INT DEFAULT 4 COMMENT '床位数量',
    occupied_beds INT DEFAULT 0 COMMENT '已入住床位',
    room_type INT DEFAULT 1 COMMENT '房间类型：1标准间 2豪华间 3其他',
    status INT DEFAULT 1 COMMENT '状态：0禁用 1启用 2维修中',
    description VARCHAR(200) COMMENT '描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
    INDEX idx_building_id (building_id),
    INDEX idx_room_number (room_number),
    INDEX idx_status (status),
    FOREIGN KEY (building_id) REFERENCES dormitory_building(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宿舍表';

-- 学生表
CREATE TABLE IF NOT EXISTS student (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT COMMENT '关联的用户ID（用于学生登录系统）',
    student_no VARCHAR(20) NOT NULL COMMENT '学号',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    gender INT DEFAULT 1 COMMENT '性别：1男 2女',
    birthday DATE COMMENT '出生日期',
    id_card VARCHAR(18) COMMENT '身份证号',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    department VARCHAR(100) COMMENT '院系',
    major VARCHAR(100) COMMENT '专业',
    class_name VARCHAR(50) COMMENT '班级',
    grade INT COMMENT '年级',
    address VARCHAR(255) COMMENT '家庭地址',
    avatar VARCHAR(255) COMMENT '头像',
    status INT DEFAULT 1 COMMENT '状态：0禁用 1启用 2已毕业',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
    UNIQUE KEY uk_student_no (student_no),
    UNIQUE KEY uk_user_id (user_id),
    INDEX idx_name (name),
    INDEX idx_department (department),
    INDEX idx_status (status),
    FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生表';

-- 入住记录表
CREATE TABLE IF NOT EXISTS checkin_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    dormitory_id BIGINT NOT NULL COMMENT '宿舍ID',
    building_id BIGINT NOT NULL COMMENT '宿舍楼ID',
    checkin_date DATE NOT NULL COMMENT '入住日期',
    checkout_date DATE COMMENT '退房日期',
    status INT DEFAULT 1 COMMENT '状态：0已退房 1入住中',
    bed_number INT COMMENT '床位号',
    remark VARCHAR(200) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
    INDEX idx_student_id (student_id),
    INDEX idx_dormitory_id (dormitory_id),
    INDEX idx_building_id (building_id),
    INDEX idx_status (status),
    INDEX idx_checkin_date (checkin_date),
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
    FOREIGN KEY (dormitory_id) REFERENCES dormitory(id) ON DELETE CASCADE,
    FOREIGN KEY (building_id) REFERENCES dormitory_building(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入住记录表';

-- 报修表
CREATE TABLE IF NOT EXISTS repair (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    student_id BIGINT COMMENT '学生ID',
    user_id BIGINT COMMENT '用户ID（报修人）',
    dormitory_id BIGINT NOT NULL COMMENT '宿舍ID',
    building_id BIGINT NOT NULL COMMENT '宿舍楼ID',
    repair_type INT DEFAULT 1 COMMENT '报修类型：1水电维修 2家具维修 3网络问题 4其他',
    title VARCHAR(100) COMMENT '报修标题',
    description VARCHAR(500) COMMENT '报修描述',
    image_urls VARCHAR(500) COMMENT '报修图片URL（多个用逗号分隔）',
    status INT DEFAULT 1 COMMENT '状态：1待处理 2处理中 3已完成 4已关闭',
    handler_id BIGINT COMMENT '处理人ID',
    handle_remark VARCHAR(500) COMMENT '处理备注',
    handle_time DATETIME COMMENT '处理时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
    INDEX idx_student_id (student_id),
    INDEX idx_user_id (user_id),
    INDEX idx_dormitory_id (dormitory_id),
    INDEX idx_building_id (building_id),
    INDEX idx_status (status),
    INDEX idx_repair_type (repair_type),
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE SET NULL,
    FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE SET NULL,
    FOREIGN KEY (dormitory_id) REFERENCES dormitory(id) ON DELETE CASCADE,
    FOREIGN KEY (building_id) REFERENCES dormitory_building(id) ON DELETE CASCADE,
    FOREIGN KEY (handler_id) REFERENCES sys_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报修表';

-- 初始化角色数据
INSERT INTO sys_role (role_name, role_code, description, status) VALUES
('超级管理员', 'admin', '系统超级管理员，拥有所有权限', 1),
('宿舍管理员', 'manager', '宿舍管理员，负责宿舍日常管理', 1),
('普通用户', 'user', '普通用户，只能查看基本信息', 1);

-- 初始化用户数据（密码：123456，使用BCrypt加密）
-- 重要：如果登录失败，请执行以下步骤：
-- 1. 运行 PasswordGenerator.java 的 main 方法生成新的 BCrypt 密码
-- 2. 执行 UPDATE 语句更新数据库：UPDATE sys_user SET password = '<新密码>' WHERE username = 'admin';
-- 
-- 以下密码是使用 BCryptPasswordEncoder 生成的示例（密码：123456）
-- 注意：由于 BCrypt 每次加密结果不同，您可能需要重新生成
INSERT INTO sys_user (username, password, real_name, phone, email, status) VALUES
('admin', '$2a$10$Eq/v3OG/28f5t.PQayKcOuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '管理员', '13800138000', 'admin@example.com', 1),
('manager', '$2a$10$Eq/v3OG/28f5t.PQayKcOuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '宿舍管理员', '13800138001', 'manager@example.com', 1);

-- 初始化用户角色关联
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1),
(2, 2);

-- 初始化宿舍楼数据
INSERT INTO dormitory_building (building_name, building_number, floor_count, room_count_per_floor, gender_type, description, status) VALUES
('1号宿舍楼', 'A1', 6, 10, 1, '男生宿舍楼', 1),
('2号宿舍楼', 'A2', 6, 10, 2, '女生宿舍楼', 1),
('3号宿舍楼', 'A3', 6, 10, 1, '男生宿舍楼', 1);

-- 初始化宿舍数据
INSERT INTO dormitory (building_id, room_number, floor, bed_count, occupied_beds, room_type, status, description) VALUES
(1, '101', 1, 4, 0, 1, 1, '标准四人间'),
(1, '102', 1, 4, 0, 1, 1, '标准四人间'),
(1, '103', 1, 4, 0, 1, 1, '标准四人间'),
(1, '201', 2, 4, 0, 1, 1, '标准四人间'),
(1, '202', 2, 4, 0, 1, 1, '标准四人间'),
(2, '101', 1, 4, 0, 1, 1, '标准四人间'),
(2, '102', 1, 4, 0, 1, 1, '标准四人间'),
(2, '201', 2, 4, 0, 1, 1, '标准四人间');

-- 初始化学生数据
INSERT INTO student (student_no, name, gender, birthday, id_card, phone, email, department, major, class_name, grade, address, status) VALUES
('2021001', '张三', 1, '2002-05-15', '110101200205151234', '13800138101', 'zhangsan@example.com', '计算机学院', '计算机科学与技术', '计科2101', 2021, '北京市朝阳区', 1),
('2021002', '李四', 1, '2002-08-20', '110101200208202345', '13800138102', 'lisi@example.com', '计算机学院', '计算机科学与技术', '计科2101', 2021, '北京市海淀区', 1),
('2021003', '王五', 2, '2002-10-10', '110101200210103456', '13800138103', 'wangwu@example.com', '外国语学院', '英语', '英语2101', 2021, '上海市浦东新区', 1),
('2021004', '赵六', 1, '2002-12-01', '110101200212014567', '13800138104', 'zhaoliu@example.com', '机械工程学院', '机械设计制造及其自动化', '机械2101', 2021, '广州市天河区', 1),
('2021005', '孙七', 2, '2002-03-25', '110101200203255678', '13800138105', 'sunqi@example.com', '经济管理学院', '会计学', '会计2101', 2021, '深圳市南山区', 1);
