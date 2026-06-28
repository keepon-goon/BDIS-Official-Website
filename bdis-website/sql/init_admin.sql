-- BDIS CMS 初始化 SQL
-- 请在 MySQL 中执行此脚本

-- 1. 管理员用户表
CREATE TABLE IF NOT EXISTS `admin_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `nickname` VARCHAR(50) DEFAULT NULL,
  `status` INT NOT NULL DEFAULT 1 COMMENT '1=启用 0=禁用',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员用户表';

-- 默认管理员账号: admin / admin123
-- 密码使用 SHA-256 加盐哈希存储，以下为预计算的哈希值
-- 请在应用启动后通过 API 或手动插入一条记录
-- 如果需要手动插入，可以先用明文测试，后续通过修改密码功能更新

-- 2. 扩展 lab_info 表（如果已存在，添加新字段）
ALTER TABLE `lab_info`
  ADD COLUMN IF NOT EXISTS `name_en` VARCHAR(200) DEFAULT NULL AFTER `lab_desc`,
  ADD COLUMN IF NOT EXISTS `intro_short` TEXT DEFAULT NULL AFTER `name_en`,
  ADD COLUMN IF NOT EXISTS `address` VARCHAR(200) DEFAULT NULL AFTER `intro_short`,
  ADD COLUMN IF NOT EXISTS `email` VARCHAR(100) DEFAULT NULL AFTER `address`,
  ADD COLUMN IF NOT EXISTS `qq_group` VARCHAR(50) DEFAULT NULL AFTER `email`,
  ADD COLUMN IF NOT EXISTS `logo_url` VARCHAR(500) DEFAULT NULL AFTER `qq_group`,
  ADD COLUMN IF NOT EXISTS `picture` VARCHAR(500) DEFAULT NULL AFTER `logo_url`;

-- 3. 插入默认实验室信息（如果不存在）
INSERT INTO `lab_info` (`id`, `lab_name`, `lab_desc`, `name_en`, `intro_short`, `address`, `email`, `qq_group`, `logo_url`, `picture`)
SELECT 1, '大数据智能工作室', '依托大数据管理与应用专业，由泰迪·重理工大数据智能工作室与重庆理工大学创新实验室联合组建',
       'Big Data Intelligence Studio · BDIS',
       '大数据智能工作室（Big Data Intelligence Studio，简称 BDIS）依托大数据管理与应用专业，由重庆理工大学大数据智能工作室与重庆理工大学创新实验室联合组建。工作室聚焦大数据、人工智能、系统开发与模型训练等方向，致力于培养具备工程实践能力与科研创新能力的高水平复合型人才。',
       '重庆理工大学 博园4号楼305', '18862028557@163.com', '935982021', NULL, NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `lab_info` WHERE `id` = 1);
