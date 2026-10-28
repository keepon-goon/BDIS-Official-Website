SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS `bdis`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE `bdis`;

CREATE TABLE IF NOT EXISTS `admin_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `nickname` VARCHAR(50) DEFAULT NULL,
  `status` INT NOT NULL DEFAULT 1,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `lab_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `lab_name` VARCHAR(200) NOT NULL,
  `lab_desc` TEXT,
  `name_en` VARCHAR(200) DEFAULT NULL,
  `intro_short` TEXT,
  `address` VARCHAR(200) DEFAULT NULL,
  `email` VARCHAR(100) DEFAULT NULL,
  `qq_group` VARCHAR(50) DEFAULT NULL,
  `logo_url` VARCHAR(500) DEFAULT NULL,
  `picture` VARCHAR(500) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `banner_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(200) NOT NULL,
  `subtitle` VARCHAR(300) DEFAULT NULL,
  `description` TEXT,
  `button_text` VARCHAR(50) DEFAULT NULL,
  `button_link` VARCHAR(500) DEFAULT NULL,
  `image_url` VARCHAR(500) DEFAULT NULL,
  `sort_order` INT DEFAULT 0,
  `active` INT NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  KEY `idx_banner_active_sort` (`active`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `news_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(200) NOT NULL,
  `category` VARCHAR(30) DEFAULT NULL,
  `summary` TEXT,
  `content` LONGTEXT,
  `publish_date` VARCHAR(20) DEFAULT NULL,
  `link` VARCHAR(500) DEFAULT NULL,
  `cover_thumb_url` VARCHAR(500) DEFAULT NULL,
  `order` INT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_news_order` (`order`),
  KEY `idx_news_category_order` (`category`, `order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `announcement_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(200) NOT NULL,
  `category` VARCHAR(30) DEFAULT NULL,
  `summary` TEXT,
  `content` LONGTEXT,
  `publish_date` VARCHAR(20) DEFAULT NULL,
  `link` VARCHAR(500) DEFAULT NULL,
  `cover_thumb_url` VARCHAR(500) DEFAULT NULL,
  `sort_order` INT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_announcement_sort` (`sort_order`),
  KEY `idx_announcement_category_sort` (`category`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `projects` (
  `id` VARCHAR(64) NOT NULL,
  `name` VARCHAR(200) NOT NULL,
  `tagline` TEXT,
  `status` VARCHAR(20) DEFAULT 'recruiting',
  `progress` INT DEFAULT 0,
  `recruit_roles` TEXT,
  `members` TEXT,
  `updated_at` VARCHAR(32) DEFAULT NULL,
  `cover_url` VARCHAR(500) DEFAULT NULL,
  `link` VARCHAR(500) DEFAULT NULL,
  `sort_order` INT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_projects_sort` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `team_member` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `role` VARCHAR(100) DEFAULT NULL,
  `description` TEXT,
  `avatar_url` VARCHAR(500) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `education_resource` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(200) NOT NULL,
  `description` TEXT,
  `sort_order` INT DEFAULT 0,
  `category` VARCHAR(30) DEFAULT NULL,
  `link` VARCHAR(500) DEFAULT NULL,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_resource_sort` (`sort_order`),
  KEY `idx_resource_category_sort` (`category`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `forum_post` (
  `id` VARCHAR(64) NOT NULL,
  `title` VARCHAR(200) NOT NULL,
  `summary` TEXT,
  `recruit_roles` TEXT,
  `current_size` INT DEFAULT 0,
  `target_size` INT DEFAULT 1,
  `type` VARCHAR(20) DEFAULT NULL,
  `value` VARCHAR(500) DEFAULT NULL,
  `deadline` VARCHAR(20) DEFAULT NULL,
  `cover_url` VARCHAR(500) DEFAULT NULL,
  `link` VARCHAR(500) DEFAULT NULL,
  `sort_order` INT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_forum_sort` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `resume_submission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(30) NOT NULL,
  `grade` VARCHAR(80) DEFAULT NULL,
  `major` VARCHAR(80) DEFAULT NULL,
  `phone` VARCHAR(30) DEFAULT NULL,
  `email` VARCHAR(254) DEFAULT NULL,
  `direction` VARCHAR(80) DEFAULT NULL,
  `intro` TEXT,
  `resume_url` VARCHAR(512) DEFAULT NULL,
  `status` INT DEFAULT 0,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `gender` VARCHAR(10) DEFAULT NULL,
  `ethnicity` VARCHAR(30) DEFAULT NULL,
  `college` VARCHAR(80) DEFAULT NULL,
  `student_no` VARCHAR(40) DEFAULT NULL,
  `birth_month` VARCHAR(7) DEFAULT NULL,
  `specialties` VARCHAR(120) DEFAULT NULL,
  `gpa` VARCHAR(20) DEFAULT NULL,
  `has_competition_experience` TINYINT DEFAULT NULL,
  `awards` TEXT,
  `showcase` TEXT,
  `academic_year` VARCHAR(20) DEFAULT NULL,
  `document_name` VARCHAR(255) DEFAULT NULL,
  `template_version` VARCHAR(64) DEFAULT NULL,
  `submission_source` VARCHAR(16) DEFAULT NULL,
  `submission_key` CHAR(64) DEFAULT NULL,
  `word_key` VARCHAR(512) DEFAULT NULL,
  `pdf_key` VARCHAR(512) DEFAULT NULL,
  `mail_status` INT DEFAULT 0,
  `submit_mail_mode` VARCHAR(10) DEFAULT NULL,
  `mail_recipient` VARCHAR(254) DEFAULT NULL,
  `mail_sent_at` DATETIME DEFAULT NULL,
  `mail_last_error` VARCHAR(1000) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_resume_submission_key` (`submission_key`),
  KEY `idx_resume_created_id` (`created_at`, `id`),
  KEY `idx_resume_mail_created` (`mail_status`, `created_at`, `id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `resume_settings` (
  `id` BIGINT NOT NULL,
  `send_mode` VARCHAR(10) NOT NULL,
  `recipient` VARCHAR(254) NOT NULL DEFAULT '',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

START TRANSACTION;

INSERT INTO `admin_user` (`id`, `username`, `password`, `nickname`, `status`)
VALUES (1, 'admin', CONCAT('bdis-demo-20261026:', SHA2(CONCAT('bdis-demo-20261026', 'BdisDemo2026!'), 256)), '演示管理员', 1)
ON DUPLICATE KEY UPDATE `id` = `id`;

INSERT INTO `lab_info`
  (`id`, `lab_name`, `lab_desc`, `name_en`, `intro_short`, `address`, `email`, `qq_group`, `logo_url`, `picture`)
VALUES
  (1, '大数据智能工作室', '<p>这里是工作室介绍的示例内容，可在管理后台替换。</p><p>工作室围绕大数据分析、人工智能与系统开发开展学习、交流和项目实践。</p>',
   'Big Data Intelligence Studio · BDIS', '以项目实践促进学习，欢迎对大数据、人工智能和软件开发感兴趣的同学加入。',
   '重庆理工大学（示例地址，请替换为实际地点）', 'bdis-demo@example.com', NULL, NULL, '/static/banners/banner1.webp')
ON DUPLICATE KEY UPDATE `id` = `id`;

INSERT INTO `banner_item`
  (`id`, `title`, `subtitle`, `description`, `button_text`, `button_link`, `image_url`, `sort_order`, `active`)
VALUES
  (1, '大数据智能工作室', '用数据理解世界，用技术实现想法', '聚焦大数据、人工智能与系统开发，在真实项目中学习与成长。',
   '了解我们', '/info/about', '/static/banners/banner1.webp', 0, 1),
  (2, '2026 秋季招新示例', '一起学习，一起完成有价值的项目', '欢迎对后端开发、前端开发和数据分析感兴趣的同学提交报名表。',
   '填写报名表', '/resume/apply', '/static/banners/banner2.webp', 1, 1)
ON DUPLICATE KEY UPDATE `id` = `id`;

INSERT INTO `news_item`
  (`id`, `title`, `category`, `summary`, `content`, `publish_date`, `link`, `cover_thumb_url`, `order`)
VALUES
  (1, '工作室官网示例站点上线', 'notice', '新闻、项目和学习资料现已集中展示。',
   '<h2>官网示例站点上线</h2><p>本条新闻用于演示列表、详情和富文本展示，可在后台编辑或删除。</p>',
   '2026-10-26', '/info/news/1', '/static/news/news1.webp', 0),
  (2, 'Java 后端学习交流活动示例', 'activity', '围绕 Spring Boot、MySQL 和接口开发开展学习交流。',
   '<h2>本次交流内容</h2><p>学习接口请求流程、数据库表设计和基础增删改查，随后进行项目实践分享。</p>',
   '2026-10-25', '/info/news/2', '/static/news/news2.webp', 1),
  (3, '数据分析项目组队示例', 'contest', '面向数据清洗、可视化和建模方向招募队员。',
   '<h2>项目介绍</h2><p>计划完成一套示例数据分析报告，欢迎对 Python、统计分析和可视化感兴趣的同学参与。</p>',
   '2026-10-24', '/info/news/3', '/static/news/news3.webp', 2)
ON DUPLICATE KEY UPDATE `id` = `id`;

INSERT INTO `announcement_item`
  (`id`, `title`, `category`, `summary`, `content`, `publish_date`, `link`, `cover_thumb_url`, `sort_order`)
VALUES
  (1, '2026 秋季工作室招新示例公告', 'recruit', '报名入口已开放，本条为演示公告。',
   '<p>请通过在线报名入口填写基本信息并预览报名表。本条内容为示例，具体安排请以实际通知为准。</p>',
   '2026-10-26', '/resume/apply', '/static/news/news4.webp', 0),
  (2, '学习资料使用说明', 'notice', '资料按前端、后端、数据库等方向分类整理。',
   '<p>学习资料页面提供不同方向的示例入口，可通过关键词和分类筛选查找内容。</p>',
   '2026-10-25', '/info/education', '/static/news/news5.webp', 1)
ON DUPLICATE KEY UPDATE `id` = `id`;

INSERT INTO `projects`
  (`id`, `name`, `tagline`, `status`, `progress`, `recruit_roles`, `members`, `updated_at`, `cover_url`, `link`, `sort_order`)
VALUES
  ('demo-project-001', '工作室官网建设示例', '使用 Spring Boot 和 Vue 3 完成内容展示与管理', 'ongoing', 70,
   'Java后端,Vue前端', '张同学（示例）,李同学（示例）', '2026-10-26', '/static/news/news6.webp', NULL, 0),
  ('demo-project-002', '校园数据分析示例', '从数据清洗到可视化报告，完成一个小型分析闭环', 'recruiting', 10,
   '数据分析,可视化', '李同学（示例）', '2026-10-25', '/static/news/news7.webp', NULL, 1)
ON DUPLICATE KEY UPDATE `id` = `id`;

INSERT INTO `team_member` (`id`, `name`, `role`, `description`, `avatar_url`)
VALUES
  (1, '张同学（示例）', '后端开发', '主要学习 Java、Spring Boot 和 MySQL，参与接口开发与数据库设计。', NULL),
  (2, '李同学（示例）', '前端与数据分析', '主要学习 Vue 3、Python 和数据可视化，参与页面开发与数据分析。', NULL)
ON DUPLICATE KEY UPDATE `id` = `id`;

INSERT INTO `education_resource` (`id`, `title`, `description`, `sort_order`, `category`, `link`)
VALUES
  (1, 'Vue 3 官方入门指南', '前端学习资料示例：了解组件、模板和响应式基础。', 0, 'frontend', 'https://cn.vuejs.org/guide/introduction.html'),
  (2, 'Spring 官方入门教程', '后端学习资料示例：通过入门教程学习 Spring 应用开发。', 1, 'backend', 'https://spring.io/guides'),
  (3, 'MySQL 8.0 官方手册', '数据库学习资料示例：查询 SQL 语法、数据类型和索引说明。', 2, 'database', 'https://dev.mysql.com/doc/refman/8.0/en/'),
  (4, 'PyTorch 官方教程', '人工智能学习资料示例：了解张量、模型训练与基础实践。', 3, 'ai', 'https://pytorch.org/tutorials/'),
  (5, 'Git 官方中文教程', '开发工具资料示例：学习提交、分支和版本管理。', 4, 'tools', 'https://git-scm.com/book/zh/v2'),
  (6, 'Python 官方中文教程', '课程资料示例：学习 Python 基础语法和常用数据结构。', 5, 'course', 'https://docs.python.org/zh-cn/3/tutorial/')
ON DUPLICATE KEY UPDATE `id` = `id`;

INSERT INTO `forum_post`
  (`id`, `title`, `summary`, `recruit_roles`, `current_size`, `target_size`, `type`, `value`, `deadline`, `cover_url`, `link`, `sort_order`)
VALUES
  ('demo-forum-001', '校园服务系统组队示例', '计划实现一个小型校园服务系统，练习接口开发与页面联调。',
   'Java后端,Vue前端', 2, 4, 'email', 'demo-team-1@example.com', '2026-11-30', '/static/news/news8.webp', NULL, 0),
  ('demo-forum-002', '数据可视化学习小组示例', '从公开数据开始，完成清洗、分析和可视化展示。',
   'Python开发,数据分析', 1, 3, 'email', 'demo-team-2@example.com', '2026-11-30', '/static/news/news9.webp', NULL, 1)
ON DUPLICATE KEY UPDATE `id` = `id`;

INSERT INTO `resume_submission`
  (`id`, `name`, `grade`, `major`, `phone`, `email`, `direction`, `intro`, `status`, `created_at`,
   `gender`, `ethnicity`, `college`, `student_no`, `birth_month`, `specialties`, `gpa`,
   `has_competition_experience`, `awards`, `showcase`, `academic_year`, `mail_status`, `submit_mail_mode`)
VALUES
  (1, '张同学（示例）', '2024级', '大数据管理与应用', '13800000001', 'demo-student-1@example.com', '后端开发',
   '这是一条用于演示后台列表和详情的虚构记录，没有对应的 Word 或 PDF 附件。', 0, '2026-10-26 10:00:00',
   '男', '汉族', '管理学院', '202400000001', '2006-03', 'Java,MySQL', '3.60',
   0, '暂无（示例）', '完成过基础增删改查练习（示例）。', '2026-2027', 0, 'MANUAL'),
  (2, '李同学（示例）', '2025级', '大数据管理与应用', '13800000002', 'demo-student-2@example.com', '数据分析',
   '这是一条用于演示后台查询的虚构记录，附件字段留空，邮件尚未发送。', 0, '2026-10-26 10:30:00',
   '女', '汉族', '管理学院', '202500000002', '2007-06', 'Python,数据可视化', '3.80',
   1, '校内数据分析练习优秀报告（示例）', '完成过一份数据清洗与可视化报告（示例）。', '2026-2027', 0, 'MANUAL')
ON DUPLICATE KEY UPDATE `id` = `id`;

INSERT INTO `resume_settings` (`id`, `send_mode`, `recipient`)
VALUES (1, 'MANUAL', '')
ON DUPLICATE KEY UPDATE `id` = `id`;

COMMIT;
