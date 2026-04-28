-- ============================================================
-- 增量升级脚本 2026-04-28
-- 功能：添加报修管理模块和学生登录权限控制
-- ============================================================

-- 1. 为学生表添加 user_id 字段（用于关联用户账号，让学生可以登录系统）
-- 如果字段已存在，此语句可能会报错，请手动处理
ALTER TABLE student ADD COLUMN user_id BIGINT COMMENT '关联的用户ID（用于学生登录系统）';
ALTER TABLE student ADD UNIQUE KEY uk_user_id (user_id);
ALTER TABLE student ADD CONSTRAINT fk_student_user FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE SET NULL;

-- 2. 为用户表添加 login_status 字段（用于控制学生登录权限）
-- 1: 允许登录  0: 禁止登录
ALTER TABLE sys_user ADD COLUMN login_status INT DEFAULT 1 COMMENT '登录状态：0禁止登录 1允许登录';

-- 3. 创建报修表
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

-- 4. 添加学生角色（如果不存在）
INSERT IGNORE INTO sys_role (role_name, role_code, description, status) VALUES
('学生', 'student', '学生角色，只能报修和查看自己的信息', 1);

-- 5. 示例：创建学生用户并关联（您需要根据实际情况修改下面的语句）
-- 注意：
-- 1. 密码需要使用 PasswordGenerator 生成正确的 BCrypt 密码
-- 2. student_no 需要对应已存在的学生学号

-- 示例：为学号 2021001 的学生创建登录账号
-- INSERT INTO sys_user (username, password, real_name, phone, email, status, login_status) VALUES
-- ('zhangsan', '<生成的BCrypt密码>', '张三', '13800138101', 'zhangsan@example.com', 1, 1);

-- 示例：关联学生和用户
-- UPDATE student SET user_id = <用户ID> WHERE student_no = '2021001';

-- 示例：分配学生角色
-- INSERT INTO sys_user_role (user_id, role_id) 
-- SELECT <用户ID>, id FROM sys_role WHERE role_code = 'student';

-- ============================================================
-- 完整示例：为学生张三（学号 2021001）创建登录账号
-- ============================================================
-- 1. 先运行 PasswordGenerator 类生成密码 "123456" 的 BCrypt 加密值
-- 2. 替换下面的 <生成的BCrypt密码>
-- 3. 执行以下 SQL：

/*
-- 创建学生用户
INSERT INTO sys_user (username, password, real_name, phone, email, status, login_status) VALUES
('zhangsan', '<生成的BCrypt密码>', '张三', '13800138101', 'zhangsan@example.com', 1, 1);

-- 获取刚插入的用户ID
-- SET @userId = LAST_INSERT_ID();

-- 关联学生和用户（假设 student_no = '2021001'）
-- UPDATE student SET user_id = @userId WHERE student_no = '2021001';

-- 分配学生角色
-- INSERT INTO sys_user_role (user_id, role_id) 
-- SELECT @userId, id FROM sys_role WHERE role_code = 'student';
*/
