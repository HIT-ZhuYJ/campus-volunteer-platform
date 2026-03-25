-- =============================================================================
-- 校园志愿服务管理平台 — 全量初始化脚本
-- 使用方式：mysql -u root -p < database/init.sql
-- 基准日期：2026-03-25（今日），测试数据覆盖所有业务场景
-- =============================================================================
-- 字段约束说明：
--   registration_start_time < registration_deadline <= start_time < end_time
--   vol_activity.current_participants  =  该活动 status=REGISTERED 的报名条数
--   sys_user.total_volunteer_hours     =  该用户所有已核销记录的 volunteer_hours 之和
--   vol_registration.status            REGISTERED / CANCELLED
-- =============================================================================

DROP DATABASE IF EXISTS volunteer_platform;
CREATE DATABASE volunteer_platform CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE volunteer_platform;

-- ==============================================================================
-- 1. 用户表
-- ==============================================================================
CREATE TABLE sys_user (
    id                   BIGINT       PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username             VARCHAR(50)  NOT NULL UNIQUE COMMENT '用户名',
    password             VARCHAR(255) NOT NULL COMMENT '密码（BCrypt）',
    real_name            VARCHAR(50)  NOT NULL COMMENT '真实姓名',
    student_no           VARCHAR(50)  COMMENT '学号',
    phone                VARCHAR(20)  COMMENT '手机号',
    email                VARCHAR(100) COMMENT '邮箱',
    role                 VARCHAR(20)  NOT NULL DEFAULT 'VOLUNTEER' COMMENT 'VOLUNTEER / ADMIN',
    total_volunteer_hours DECIMAL(10,2) DEFAULT 0.00 COMMENT '累计志愿时长（小时）',
    status               TINYINT      DEFAULT 1 COMMENT '0-禁用 1-启用',
    create_time          DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time          DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username  (username),
    INDEX idx_student_no (student_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 密码均为：password123
-- total_volunteer_hours = 该用户所有已核销（hours_confirmed=1）记录的 volunteer_hours 汇总
INSERT INTO sys_user (username, password, real_name, student_no, phone, email, role, total_volunteer_hours, status) VALUES
-- id=1  管理员
('admin',     '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '管理员',  '2021001', '13800138000', 'admin@university.edu',    'ADMIN',     0.00,  1),
-- id=2  张三：已核销 活动12(2h)+活动15(3h)+活动17(4h)+活动18(2h) = 11.00h
('student01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '张三',    '2021101', '13800138001', 'zhangsan@university.edu', 'VOLUNTEER', 11.00,  1),
-- id=3  李四：已核销 活动15(3h)+活动19(3h) = 6.00h
('student02', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '李四',    '2021102', '13800138002', 'lisi@university.edu',     'VOLUNTEER',  6.00,  1),
-- id=4  王五：已核销 活动16(2.5h)+活动17(4h) = 6.50h
('student03', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '王五',    '2021103', '13800138003', 'wangwu@university.edu',   'VOLUNTEER',  6.50,  1),
-- id=5  赵六：已核销 活动15(3h)+活动17(4h) = 7.00h
('student04', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '赵六',    '2021104', '13800138004', 'zhaoliu@university.edu',  'VOLUNTEER',  7.00,  1),
-- id=6  陈七：已核销 活动12(2h)+活动16(2.5h)+活动18(2h) = 6.50h
('student05', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '陈七',    '2021105', '13800138005', 'chenqi@university.edu',   'VOLUNTEER',  6.50,  1),
-- id=7  孙八：已核销 活动15(3h)+活动16(2.5h) = 5.50h
('student06', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '孙八',    '2021106', '13800138006', 'sunba@university.edu',    'VOLUNTEER',  5.50,  1),
-- id=8  周九：尚未核销 0h
('student07', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '周九',    '2021107', '13800138007', 'zhoujiu@university.edu',  'VOLUNTEER',  0.00,  1),
-- id=9  吴十：尚未核销 0h
('student08', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '吴十',    '2021108', '13800138008', 'wushi@university.edu',    'VOLUNTEER',  0.00,  1),
-- id=10 郑十一：尚未核销 0h
('student09', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '郑十一',  '2021109', '13800138009', 'zhengsy@university.edu',  'VOLUNTEER',  0.00,  1),
-- id=11 王十二：尚未核销 0h
('student10', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '王十二',  '2021110', '13800138010', 'wangse@university.edu',   'VOLUNTEER',  0.00,  1);

-- ==============================================================================
-- 2. 志愿活动表
-- ==============================================================================
CREATE TABLE vol_activity (
    id                     BIGINT        PRIMARY KEY AUTO_INCREMENT COMMENT '活动ID',
    title                  VARCHAR(200)  NOT NULL COMMENT '活动标题',
    description            TEXT          COMMENT '活动详情',
    location               VARCHAR(200)  NOT NULL COMMENT '服务地点',
    max_participants       INT           NOT NULL COMMENT '招募上限',
    current_participants   INT           DEFAULT 0 COMMENT '当前有效报名人数',
    volunteer_hours        DECIMAL(5,2)  NOT NULL COMMENT '志愿时长（小时）',
    start_time             DATETIME      NOT NULL COMMENT '活动开始时间',
    end_time               DATETIME      NOT NULL COMMENT '活动结束时间',
    registration_start_time DATETIME     NOT NULL COMMENT '招募开始时间',
    registration_deadline  DATETIME      NOT NULL COMMENT '招募截止时间',
    status                 VARCHAR(20)   NOT NULL DEFAULT 'RECRUITING'
                           COMMENT '状态：RECRUITING-招募中, COMPLETED-已结项, CANCELLED-已取消（进行中阶段由时间动态判断）',
    category               VARCHAR(50)   COMMENT '活动类型',
    creator_id             BIGINT        NOT NULL COMMENT '创建人ID',
    create_time            DATETIME      DEFAULT CURRENT_TIMESTAMP,
    update_time            DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_status                (status),
    INDEX idx_category              (category),
    INDEX idx_start_time            (start_time),
    INDEX idx_end_time              (end_time),
    INDEX idx_registration_start    (registration_start_time),
    INDEX idx_registration_deadline (registration_deadline),
    INDEX idx_creator_id            (creator_id),
    FOREIGN KEY (creator_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='志愿活动表';

-- ------------------------------------------------------------------------------
-- 活动数据（基准日期 2026-03-25）
--
-- 场景一：【招募中】registration_start <= 2026-03-25 <= registration_deadline
--   排序按 registration_deadline ASC，截止最近的排在最前
--
-- 场景二：【招募未开始】registration_start > 2026-03-25
--
-- 场景三：【招募已截止，活动未开始】registration_deadline < 2026-03-25 < start_time
--
-- 场景四：【活动进行中】start_time <= 2026-03-25 <= end_time（供签到模块使用）
--
-- 场景五：【活动已结束，待核销】end_time < 2026-03-25，status=RECRUITING
--          （供时长核销模块使用）
--
-- 场景六：【已结项 COMPLETED】
--
-- 场景七：【已取消 CANCELLED】
-- ------------------------------------------------------------------------------

INSERT INTO vol_activity (
    title, description, location,
    max_participants, current_participants, volunteer_hours,
    start_time, end_time,
    registration_start_time, registration_deadline,
    status, category, creator_id
) VALUES

-- ========== 场景一：招募中 ==========

-- id=1  最紧急，3天后截止
('暖冬行动 - 老人院关爱探访',
 '前往附近养老院陪伴老人，开展手工制作、文艺表演、心理关怀等温情活动。要求：耐心、责任心强，提前参加培训。',
 '阳光养老院', 20, 3, 3.00,
 '2026-04-05 09:00:00', '2026-04-05 17:00:00',
 '2026-03-01 00:00:00', '2026-03-28 23:59:59',
 'RECRUITING', '暖冬行动', 1),

-- id=2  5天后截止
('爱心小屋 - 留守儿童春日慰问',
 '走进周边村镇小学，为留守儿童开展课外辅导、绘本阅读及亲情视频连线陪伴服务。',
 '希望小学操场', 15, 4, 4.00,
 '2026-04-10 08:30:00', '2026-04-10 17:30:00',
 '2026-03-05 00:00:00', '2026-03-30 23:59:59',
 'RECRUITING', '爱心小屋', 1),

-- id=3  11天后截止
('书记驿站 - 社区服务中心值班',
 '在学校书记驿站社区服务中心承担日常咨询、资料整理、活动协助等志愿工作，每周六上午轮班。',
 '书记驿站社区服务中心', 10, 2, 3.00,
 '2026-04-15 09:00:00', '2026-04-15 12:00:00',
 '2026-03-01 00:00:00', '2026-04-05 23:59:59',
 'RECRUITING', '书记驿站', 1),

-- id=4  16天后截止
('校友招商 - 春季人才招聘会志愿者',
 '协助组织春季校园人才招聘大会，负责入场引导、企业接待、现场秩序维护及问卷统计等工作。着装：正装。',
 '大学生活动中心展厅', 30, 2, 5.00,
 '2026-04-20 08:00:00', '2026-04-20 18:00:00',
 '2026-03-01 00:00:00', '2026-04-10 23:59:59',
 'RECRUITING', '校友招商', 1),

-- id=5  26天后截止
('学长火炬 - 毕业典礼志愿者',
 '参与年度毕业典礼志愿服务，协助布置会场、引导嘉宾入席、颁发纪念品及现场摄影记录。',
 '大礼堂', 40, 1, 4.00,
 '2026-05-15 09:00:00', '2026-05-15 18:00:00',
 '2026-03-15 00:00:00', '2026-04-20 23:59:59',
 'RECRUITING', '学长火炬', 1),

-- ========== 场景二：招募未开始 ==========

-- id=6  招募5月才开始
('书记驿站 - 图书馆暑期专项服务',
 '暑期图书馆志愿服务，协助读者检索、图书整理、书目更新及阅览室秩序维护，每周提供3个灵活排班。',
 '图书馆二楼服务台', 12, 0, 3.50,
 '2026-06-20 09:00:00', '2026-06-20 17:30:00',
 '2026-05-01 00:00:00', '2026-06-15 23:59:59',
 'RECRUITING', '书记驿站', 1),

-- id=7  招募7月才开始
('学长火炬 - 新生入学迎新服务',
 '迎接新生入学，协助办理报到手续、行李搬运、校园引导、答疑咨询及宿舍分配协助。',
 '学校东门迎新广场', 60, 0, 4.00,
 '2026-09-01 07:30:00', '2026-09-01 20:00:00',
 '2026-07-01 00:00:00', '2026-08-20 23:59:59',
 'RECRUITING', '学长火炬', 1),

-- ========== 场景三：招募截止，活动未开始 ==========

-- id=8  招募截止 3月10日，活动4月1日
('爱心小屋 - 献血车志愿服务',
 '在校医院门口献血车旁提供引导、宣传、爱心礼品发放及献血后关怀服务，共同传递生命温暖。',
 '校医院献血车旁', 20, 3, 2.50,
 '2026-04-01 09:00:00', '2026-04-01 17:00:00',
 '2026-02-20 00:00:00', '2026-03-10 23:59:59',
 'RECRUITING', '爱心小屋', 1),

-- id=9  招募截止 3月15日，活动4月8日
('暖冬行动 - 乡村小学春季支教',
 '前往郊区乡村小学开展为期一天的公益支教活动，科目涵盖语文、数学、美术，同时捐赠学习物资。',
 '青山路小学', 25, 5, 6.00,
 '2026-04-08 08:00:00', '2026-04-08 18:00:00',
 '2026-02-01 00:00:00', '2026-03-15 23:59:59',
 'RECRUITING', '暖冬行动', 1),

-- ========== 场景四：活动进行中（签到模块可见） ==========

-- id=10  今日进行中（2026-03-25 全天）
('学长火炬 - 校园运动会引导服务',
 '春季运动会全天志愿服务：入口检票、赛场引导、颁奖礼仪、赛后清洁，全程统一着装。',
 '田径运动场', 30, 3, 4.00,
 '2026-03-25 07:30:00', '2026-03-25 20:00:00',
 '2026-03-01 00:00:00', '2026-03-20 23:59:59',
 'RECRUITING', '学长火炬', 1),

-- id=11  跨天进行中（3月24日开始，3月26日结束）
('书记驿站 - 校园文化艺术节志愿服务',
 '三天文化艺术节志愿者，参与展览布置、观众引导、节目主持支持及现场拍摄记录工作。',
 '学生活动中心广场', 25, 4, 5.00,
 '2026-03-24 08:00:00', '2026-03-26 21:00:00',
 '2026-03-05 00:00:00', '2026-03-22 23:59:59',
 'RECRUITING', '书记驿站', 1),

-- ========== 场景五：活动已结束，待核销（时长核销模块可见） ==========

-- id=12  上周刚结束（3月10日），部分人已确认时长
('爱心小屋 - 图书爱心捐赠活动',
 '联合多家书店在校内举办爱心图书捐赠市集，收集学生旧书并整理后转赠给山区小学。',
 '图书馆一楼大厅', 20, 5, 2.00,
 '2026-03-10 10:00:00', '2026-03-10 17:00:00',
 '2026-02-20 00:00:00', '2026-03-05 23:59:59',
 'RECRUITING', '爱心小屋', 1),

-- id=13  上月底结束（2月28日），部分人已确认时长
('暖冬行动 - 元宵节义卖活动',
 '元宵节校园义卖，学生志愿者手工制作灯笼及汤圆并摆摊义卖，所得善款捐赠贫困学子。',
 '校园中心广场', 20, 4, 3.00,
 '2026-02-28 10:00:00', '2026-02-28 16:00:00',
 '2026-02-01 00:00:00', '2026-02-20 23:59:59',
 'RECRUITING', '暖冬行动', 1),

-- id=14  今年1月结束，尚未核销
('校友招商 - 年度颁奖典礼接待志愿',
 '年度校友颁奖典礼全程接待志愿者，负责来宾签到、座位引导、嘉宾茶水服务及媒体联络协助。',
 '国际报告厅', 15, 3, 5.00,
 '2026-01-15 13:00:00', '2026-01-15 18:00:00',
 '2025-12-20 00:00:00', '2026-01-05 23:59:59',
 'RECRUITING', '校友招商', 1),

-- ========== 场景六：已结项 COMPLETED ==========

-- id=15  去年11月结项，全部核销完毕
('学长火炬 - 校庆七十周年纪念日引导',
 '学校七十周年校庆庆典全程志愿服务，参与典礼仪式引导、历史展览讲解及来宾接待等工作。',
 '校史馆广场', 50, 5, 3.00,
 '2025-11-01 08:00:00', '2025-11-01 18:00:00',
 '2025-09-15 00:00:00', '2025-10-25 23:59:59',
 'COMPLETED', '学长火炬', 1),

-- id=16  去年12月结项，全部核销完毕
('书记驿站 - 年度学术论坛协助',
 '协助承办年度大型学术论坛，负责会场布置、嘉宾接待、资料印发、茶歇安排及会议记录整理。',
 '学术交流中心', 30, 4, 2.50,
 '2025-12-20 08:30:00', '2025-12-20 18:00:00',
 '2025-11-01 00:00:00', '2025-12-10 23:59:59',
 'COMPLETED', '书记驿站', 1),

-- id=17  今年1月结项，全部核销完毕
('暖冬行动 - 春节前夕送温暖活动',
 '春节前走访校内困难家庭及留守学生，派发新春礼包、开展文艺表演，传递新年温情。',
 '学生公寓一号楼大厅', 20, 3, 4.00,
 '2026-01-18 10:00:00', '2026-01-18 16:00:00',
 '2025-12-15 00:00:00', '2026-01-10 23:59:59',
 'COMPLETED', '暖冬行动', 1),

-- id=18  今年2月结项，部分核销完毕
('爱心小屋 - 新春联欢义演',
 '组织志愿者在周边社区举办新春联欢演出，表演节目涵盖歌舞、小品、书法展示等文艺形式。',
 '社区文化活动中心', 18, 5, 2.00,
 '2026-02-05 14:00:00', '2026-02-05 18:00:00',
 '2026-01-10 00:00:00', '2026-01-30 23:59:59',
 'COMPLETED', '爱心小屋', 1),

-- ========== 场景七：已取消 CANCELLED ==========

-- id=19  因场地原因取消
('校友招商 - 大型校企合作论坛（已取消）',
 '原定举办的大型校企合作对接论坛因会场临时变更，现已取消本次志愿招募，感谢报名同学的支持。',
 '综合楼多功能厅', 50, 0, 6.00,
 '2026-03-20 09:00:00', '2026-03-20 17:00:00',
 '2026-02-01 00:00:00', '2026-03-12 23:59:59',
 'CANCELLED', '校友招商', 1),

-- id=20  因合作方原因取消
('暖冬行动 - 暑期公益夏令营（已取消）',
 '原定与公益机构合作开展暑期夏令营，因合作方项目调整，本次招募已取消，欢迎关注后续活动。',
 '线上说明会', 30, 0, 8.00,
 '2026-07-15 09:00:00', '2026-07-15 18:00:00',
 '2026-05-01 00:00:00', '2026-06-30 23:59:59',
 'CANCELLED', '暖冬行动', 1);

-- ==============================================================================
-- 3. 报名流水表
-- ==============================================================================
CREATE TABLE vol_registration (
    id               BIGINT      PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    user_id          BIGINT      NOT NULL COMMENT '用户ID',
    activity_id      BIGINT      NOT NULL COMMENT '活动ID',
    registration_time DATETIME   DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
    check_in_status  TINYINT     DEFAULT 0 COMMENT '0-未签到 1-已签到',
    check_in_time    DATETIME    COMMENT '签到时间',
    hours_confirmed  TINYINT     DEFAULT 0 COMMENT '0-未核销 1-已核销',
    confirm_time     DATETIME    COMMENT '核销时间',
    status           VARCHAR(20) DEFAULT 'REGISTERED' COMMENT 'REGISTERED / CANCELLED',
    create_time      DATETIME    DEFAULT CURRENT_TIMESTAMP,
    update_time      DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_activity (user_id, activity_id),
    INDEX idx_user_id     (user_id),
    INDEX idx_activity_id (activity_id),
    INDEX idx_status      (status),
    FOREIGN KEY (user_id)     REFERENCES sys_user(id),
    FOREIGN KEY (activity_id) REFERENCES vol_activity(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报名流水表';

-- ==============================================================================
-- 报名数据
-- 每个 activity_id 的 REGISTERED 条数 = vol_activity.current_participants
-- 已核销记录对应 sys_user.total_volunteer_hours 已汇总
-- ==============================================================================

INSERT INTO vol_registration
    (user_id, activity_id, registration_time, check_in_status, check_in_time, hours_confirmed, confirm_time, status)
VALUES

-- ---------- 活动1：暖冬行动 - 老人院关爱探访（招募中，cur=3）----------
(2,  1, '2026-03-01 10:00:00', 0, NULL, 0, NULL, 'REGISTERED'),  -- 张三
(3,  1, '2026-03-05 14:30:00', 0, NULL, 0, NULL, 'REGISTERED'),  -- 李四
(4,  1, '2026-03-10 09:15:00', 0, NULL, 0, NULL, 'REGISTERED'),  -- 王五

-- ---------- 活动2：爱心小屋 - 留守儿童春日慰问（招募中，cur=4）----------
(5,  2, '2026-03-08 11:00:00', 0, NULL, 0, NULL, 'REGISTERED'),  -- 赵六
(6,  2, '2026-03-12 16:00:00', 0, NULL, 0, NULL, 'REGISTERED'),  -- 陈七
(8,  2, '2026-03-15 10:30:00', 0, NULL, 0, NULL, 'REGISTERED'),  -- 周九
(9,  2, '2026-03-20 09:00:00', 0, NULL, 0, NULL, 'REGISTERED'),  -- 吴十

-- ---------- 活动3：书记驿站 - 社区服务中心值班（招募中，cur=2）----------
(2,  3, '2026-03-15 14:00:00', 0, NULL, 0, NULL, 'REGISTERED'),  -- 张三
(7,  3, '2026-03-20 11:20:00', 0, NULL, 0, NULL, 'REGISTERED'),  -- 孙八

-- ---------- 活动4：校友招商 - 春季人才招聘会（招募中，cur=2）----------
(3,  4, '2026-03-02 10:00:00', 0, NULL, 0, NULL, 'REGISTERED'),  -- 李四
(11, 4, '2026-03-08 15:00:00', 0, NULL, 0, NULL, 'REGISTERED'),  -- 王十二

-- ---------- 活动5：学长火炬 - 毕业典礼志愿者（招募中，cur=1）----------
(4,  5, '2026-03-18 09:30:00', 0, NULL, 0, NULL, 'REGISTERED'),  -- 王五

-- ---------- 活动6、7：招募未开始，cur=0，无报名 ----------

-- ---------- 活动8：爱心小屋 - 献血车志愿服务（招募截止，cur=3）----------
(5,  8, '2026-02-22 10:00:00', 0, NULL, 0, NULL, 'REGISTERED'),  -- 赵六
(7,  8, '2026-02-25 11:30:00', 0, NULL, 0, NULL, 'REGISTERED'),  -- 孙八
(10, 8, '2026-03-01 09:00:00', 0, NULL, 0, NULL, 'REGISTERED'),  -- 郑十一

-- ---------- 活动9：暖冬行动 - 乡村小学春季支教（招募截止，cur=5）----------
(2,  9, '2026-02-03 10:00:00', 0, NULL, 0, NULL, 'REGISTERED'),  -- 张三
(3,  9, '2026-02-05 14:30:00', 0, NULL, 0, NULL, 'REGISTERED'),  -- 李四
(4,  9, '2026-02-08 09:00:00', 0, NULL, 0, NULL, 'REGISTERED'),  -- 王五
(6,  9, '2026-02-10 16:00:00', 0, NULL, 0, NULL, 'REGISTERED'),  -- 陈七
(9,  9, '2026-03-01 10:00:00', 0, NULL, 0, NULL, 'REGISTERED'),  -- 吴十

-- ---------- 活动10：学长火炬 - 运动会（进行中，cur=3）签到情况不一 ----------
(4,  10, '2026-03-05 10:00:00', 1, '2026-03-25 07:45:00', 0, NULL, 'REGISTERED'),  -- 王五 已签到
(6,  10, '2026-03-08 14:00:00', 0, NULL,                  0, NULL, 'REGISTERED'),  -- 陈七 未签到
(8,  10, '2026-03-10 11:00:00', 1, '2026-03-25 07:50:00', 0, NULL, 'REGISTERED'),  -- 周九 已签到

-- ---------- 活动11：书记驿站 - 文化艺术节（进行中，cur=4）签到情况不一 ----------
(2,  11, '2026-03-06 09:00:00', 1, '2026-03-24 07:55:00', 0, NULL, 'REGISTERED'),  -- 张三 已签到
(3,  11, '2026-03-07 14:00:00', 1, '2026-03-24 08:00:00', 0, NULL, 'REGISTERED'),  -- 李四 已签到
(5,  11, '2026-03-09 10:00:00', 0, NULL,                  0, NULL, 'REGISTERED'),  -- 赵六 未签到
(10, 11, '2026-03-12 16:00:00', 1, '2026-03-24 08:10:00', 0, NULL, 'REGISTERED'),  -- 郑十一 已签到

-- ---------- 活动12：爱心小屋 - 图书捐赠（已结束待核销，cur=5）----------
-- volunteer_hours=2.00；张三(u2)+陈七(u6)已核销，其余待处理
(2,  12, '2026-02-21 10:00:00', 1, '2026-03-10 10:05:00', 1, '2026-03-10 18:30:00', 'REGISTERED'),  -- 张三 已核销 +2h
(6,  12, '2026-02-22 14:00:00', 1, '2026-03-10 10:10:00', 1, '2026-03-10 18:30:00', 'REGISTERED'),  -- 陈七 已核销 +2h
(7,  12, '2026-02-23 11:00:00', 1, '2026-03-10 10:15:00', 0, NULL,                  'REGISTERED'),  -- 孙八 待核销
(9,  12, '2026-02-24 09:00:00', 0, NULL,                  0, NULL,                  'REGISTERED'),  -- 吴十 未签到
(11, 12, '2026-02-25 16:00:00', 1, '2026-03-10 10:20:00', 0, NULL,                  'REGISTERED'),  -- 王十二 待核销

-- ---------- 活动13：暖冬行动 - 元宵节义卖（已结束待核销，cur=4）----------
-- volunteer_hours=3.00；李四(u3)已核销
(3,  13, '2026-02-02 10:00:00', 1, '2026-02-28 10:05:00', 1, '2026-02-28 17:00:00', 'REGISTERED'),  -- 李四 已核销 +3h
(5,  13, '2026-02-05 14:00:00', 1, '2026-02-28 10:10:00', 0, NULL,                  'REGISTERED'),  -- 赵六 待核销
(8,  13, '2026-02-08 11:00:00', 0, NULL,                  0, NULL,                  'REGISTERED'),  -- 周九 未签到
(10, 13, '2026-02-10 09:00:00', 1, '2026-02-28 10:20:00', 0, NULL,                  'REGISTERED'),  -- 郑十一 待核销

-- ---------- 活动14：校友招商 - 年度颁奖典礼（已结束待核销，cur=3）----------
-- volunteer_hours=5.00；全部已签到但尚未核销
(4,  14, '2025-12-22 10:00:00', 1, '2026-01-15 13:05:00', 0, NULL, 'REGISTERED'),  -- 王五 待核销
(6,  14, '2025-12-23 14:00:00', 0, NULL,                  0, NULL, 'REGISTERED'),  -- 陈七 未签到
(11, 14, '2025-12-25 09:00:00', 1, '2026-01-15 13:10:00', 0, NULL, 'REGISTERED'),  -- 王十二 待核销

-- ---------- 活动15：学长火炬 - 校庆（COMPLETED，cur=5）全部完结----------
-- volunteer_hours=3.00；张三(u2)+李四(u3)+赵六(u5)+孙八(u7)已核销，吴十(u9)未签到
(2,  15, '2025-09-16 10:00:00', 1, '2025-11-01 08:10:00', 1, '2025-11-01 19:00:00', 'REGISTERED'),  -- 张三 +3h
(3,  15, '2025-09-18 11:00:00', 1, '2025-11-01 08:15:00', 1, '2025-11-01 19:00:00', 'REGISTERED'),  -- 李四 +3h
(5,  15, '2025-09-20 14:00:00', 1, '2025-11-01 08:20:00', 1, '2025-11-01 19:00:00', 'REGISTERED'),  -- 赵六 +3h
(7,  15, '2025-09-22 09:00:00', 1, '2025-11-01 08:25:00', 1, '2025-11-01 19:00:00', 'REGISTERED'),  -- 孙八 +3h
(9,  15, '2025-09-25 16:00:00', 0, NULL,                  0, NULL,                  'REGISTERED'),  -- 吴十 未签到

-- ---------- 活动16：书记驿站 - 学术论坛（COMPLETED，cur=4）全部完结----------
-- volunteer_hours=2.50；王五(u4)+陈七(u6)+孙八(u7)已核销，周九(u8)未签到
(4,  16, '2025-11-02 10:00:00', 1, '2025-12-20 08:35:00', 1, '2025-12-20 19:00:00', 'REGISTERED'),  -- 王五 +2.5h
(6,  16, '2025-11-05 14:00:00', 1, '2025-12-20 08:40:00', 1, '2025-12-20 19:00:00', 'REGISTERED'),  -- 陈七 +2.5h
(7,  16, '2025-11-08 11:00:00', 1, '2025-12-20 08:45:00', 1, '2025-12-20 19:00:00', 'REGISTERED'),  -- 孙八 +2.5h
(8,  16, '2025-11-10 09:00:00', 0, NULL,                  0, NULL,                  'REGISTERED'),  -- 周九 未签到

-- ---------- 活动17：暖冬行动 - 春节前夕送温暖（COMPLETED，cur=3）全部完结----------
-- volunteer_hours=4.00；张三(u2)+王五(u4)+赵六(u5)已核销
(2,  17, '2025-12-16 10:00:00', 1, '2026-01-18 10:05:00', 1, '2026-01-18 17:00:00', 'REGISTERED'),  -- 张三 +4h
(4,  17, '2025-12-18 14:00:00', 1, '2026-01-18 10:10:00', 1, '2026-01-18 17:00:00', 'REGISTERED'),  -- 王五 +4h
(5,  17, '2025-12-20 11:00:00', 1, '2026-01-18 10:15:00', 1, '2026-01-18 17:00:00', 'REGISTERED'),  -- 赵六 +4h

-- ---------- 活动18：爱心小屋 - 新春联欢义演（COMPLETED，cur=5）部分核销----------
-- volunteer_hours=2.00；张三(u2)+陈七(u6)已核销，其余已签到但未核销
(2,  18, '2026-01-11 10:00:00', 1, '2026-02-05 14:05:00', 1, '2026-02-05 19:00:00', 'REGISTERED'),  -- 张三 +2h
(3,  18, '2026-01-12 11:00:00', 1, '2026-02-05 14:10:00', 0, NULL,                  'REGISTERED'),  -- 李四 待核销
(5,  18, '2026-01-13 14:00:00', 1, '2026-02-05 14:15:00', 0, NULL,                  'REGISTERED'),  -- 赵六 待核销
(6,  18, '2026-01-15 09:00:00', 1, '2026-02-05 14:20:00', 1, '2026-02-05 19:00:00', 'REGISTERED'),  -- 陈七 +2h
(10, 18, '2026-01-18 16:00:00', 1, '2026-02-05 14:25:00', 0, NULL,                  'REGISTERED');  -- 郑十一 待核销

-- 活动19、20：CANCELLED，current_participants=0，无报名记录

-- ==============================================================================
-- 4. 视图：活动统计（供 BI/报表查询使用，应用服务层未直接引用）
-- ==============================================================================
CREATE OR REPLACE VIEW v_activity_statistics AS
SELECT
    a.id,
    a.title,
    a.category,
    a.status,
    a.max_participants,
    a.current_participants,
    ROUND((a.current_participants / NULLIF(a.max_participants, 0)) * 100, 2) AS enrollment_rate,
    COUNT(DISTINCT r.user_id)                                                 AS actual_registrations,
    SUM(CASE WHEN r.check_in_status = 1 THEN 1 ELSE 0 END)                   AS checked_in_count,
    SUM(CASE WHEN r.hours_confirmed  = 1 THEN 1 ELSE 0 END)                   AS hours_confirmed_count
FROM vol_activity a
LEFT JOIN vol_registration r ON a.id = r.activity_id AND r.status = 'REGISTERED'
GROUP BY a.id, a.title, a.category, a.status, a.max_participants, a.current_participants;
