-- 校园志愿服务管理平台数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS volunteer_platform CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE volunteer_platform;

-- ==================== 用户表 ====================
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名/学号',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密）',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    student_no VARCHAR(50) COMMENT '学号',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    role VARCHAR(20) NOT NULL DEFAULT 'VOLUNTEER' COMMENT '角色：VOLUNTEER-志愿者, ADMIN-管理员',
    total_volunteer_hours DECIMAL(10,2) DEFAULT 0.00 COMMENT '累计志愿时长（小时）',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_student_no (student_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 插入测试数据
INSERT INTO sys_user (username, password, real_name, student_no, phone, email, role, total_volunteer_hours) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '管理员', '2021001', '13800138000', 'admin@university.edu', 'ADMIN', 0.00),
('student01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '张三', '2021101', '13800138001', 'zhangsan@university.edu', 'VOLUNTEER', 12.50),
('student02', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '李四', '2021102', '13800138002', 'lisi@university.edu', 'VOLUNTEER', 8.00);
-- 密码均为: password123

-- ==================== 志愿活动表 ====================
DROP TABLE IF EXISTS vol_activity;
CREATE TABLE vol_activity (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '活动ID',
    title VARCHAR(200) NOT NULL COMMENT '活动标题',
    description TEXT COMMENT '活动详细描述',
    location VARCHAR(200) NOT NULL COMMENT '服务地点',
    max_participants INT NOT NULL COMMENT '招募人数上限',
    current_participants INT DEFAULT 0 COMMENT '当前报名人数',
    volunteer_hours DECIMAL(5,2) NOT NULL COMMENT '志愿时长（小时）',
    start_time DATETIME NOT NULL COMMENT '活动开始时间',
    end_time DATETIME NOT NULL COMMENT '活动结束时间',
    registration_deadline DATETIME NOT NULL COMMENT '报名截止时间',
    status VARCHAR(20) NOT NULL DEFAULT 'RECRUITING' COMMENT '状态：RECRUITING-招募中, ONGOING-进行中, COMPLETED-已结项, CANCELLED-已取消',
    category VARCHAR(50) COMMENT '活动类型：学长火炬、书记驿站、爱心小屋、校友招商、暖冬行动等',
    creator_id BIGINT NOT NULL COMMENT '创建人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_status (status),
    INDEX idx_category (category),
    INDEX idx_start_time (start_time),
    INDEX idx_creator_id (creator_id),
    FOREIGN KEY (creator_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='志愿活动表';

-- 插入测试数据
INSERT INTO vol_activity (title, description, location, max_participants, current_participants, volunteer_hours, start_time, end_time, registration_deadline, status, category, creator_id) VALUES
('学长火炬 - 新生入学引导', '协助2024级新生办理入学手续，提供校园导览服务，解答新生疑问。要求：热情、熟悉校园环境、沟通能力强。', '学校东门迎新点', 50, 35, 4.00, '2024-09-01 08:00:00', '2024-09-01 18:00:00', '2024-08-25 23:59:59', 'RECRUITING', '学长火炬', 1),
('书记驿站 - 图书馆值班', '在图书馆书记驿站值班，为同学提供咨询服务，维护阅览秩序，协助图书整理工作。', '图书馆三楼书记驿站', 20, 18, 3.00, '2024-10-15 14:00:00', '2024-10-15 20:00:00', '2024-10-10 23:59:59', 'RECRUITING', '书记驿站', 1),
('爱心小屋献血车志愿服务', '在献血车旁协助献血流程引导、提供爱心服务、发放纪念品。要求：有爱心、耐心细致。', '校医院门口献血车', 15, 12, 2.50, '2024-11-20 09:00:00', '2024-11-20 17:00:00', '2024-11-15 23:59:59', 'RECRUITING', '爱心小屋', 1),
('校友招商大会引导服务', '负责校友招商大会的会场引导、嘉宾接待、资料发放等工作。着装要求：正装。', '国际会议中心', 30, 5, 5.00, '2024-12-10 08:00:00', '2024-12-10 18:00:00', '2024-12-05 23:59:59', 'RECRUITING', '校友招商', 1),
('暖冬行动 - 关爱留守儿童', '前往周边乡村小学，为留守儿童提供学业辅导、心理陪伴，捐赠冬季物资。', '阳光乡村小学', 25, 20, 6.00, '2024-12-25 09:00:00', '2024-12-25 16:00:00', '2024-12-20 23:59:59', 'RECRUITING', '暖冬行动', 1);

-- ==================== 报名流水表 ====================
DROP TABLE IF EXISTS vol_registration;
CREATE TABLE vol_registration (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '报名记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    activity_id BIGINT NOT NULL COMMENT '活动ID',
    registration_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
    check_in_status TINYINT DEFAULT 0 COMMENT '签到状态：0-未签到，1-已签到',
    check_in_time DATETIME COMMENT '签到时间',
    hours_confirmed TINYINT DEFAULT 0 COMMENT '时长核销状态：0-未核销，1-已核销',
    confirm_time DATETIME COMMENT '核销时间',
    status VARCHAR(20) DEFAULT 'REGISTERED' COMMENT '状态：REGISTERED-已报名, CANCELLED-已取消',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_user_activity (user_id, activity_id),
    INDEX idx_user_id (user_id),
    INDEX idx_activity_id (activity_id),
    INDEX idx_status (status),
    FOREIGN KEY (user_id) REFERENCES sys_user(id),
    FOREIGN KEY (activity_id) REFERENCES vol_activity(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报名流水表';

-- 插入测试数据
INSERT INTO vol_registration (user_id, activity_id, check_in_status, check_in_time, hours_confirmed, confirm_time, status) VALUES
(2, 1, 1, '2024-09-01 08:15:00', 1, '2024-09-01 19:00:00', 'REGISTERED'),
(3, 1, 1, '2024-09-01 08:20:00', 1, '2024-09-01 19:00:00', 'REGISTERED'),
(2, 2, 0, NULL, 0, NULL, 'REGISTERED'),
(3, 3, 0, NULL, 0, NULL, 'REGISTERED');

-- ==================== 视图：活动统计 ====================
CREATE OR REPLACE VIEW v_activity_statistics AS
SELECT 
    a.id,
    a.title,
    a.category,
    a.status,
    a.max_participants,
    a.current_participants,
    ROUND((a.current_participants / a.max_participants) * 100, 2) AS enrollment_rate,
    COUNT(DISTINCT r.user_id) AS actual_registrations,
    SUM(CASE WHEN r.check_in_status = 1 THEN 1 ELSE 0 END) AS checked_in_count,
    SUM(CASE WHEN r.hours_confirmed = 1 THEN 1 ELSE 0 END) AS hours_confirmed_count
FROM vol_activity a
LEFT JOIN vol_registration r ON a.id = r.activity_id AND r.status = 'REGISTERED'
GROUP BY a.id;
