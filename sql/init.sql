-- =============================================
-- 空气质量分析平台数据库初始化脚本
-- 数据库: airquality_db
-- 字符集: utf8mb4
-- 引擎: InnoDB
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS airquality_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE airquality_db;

-- =============================================
-- 1. 用户表 (user)
-- =============================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码(MD5加密)',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `birthday` DATE DEFAULT NULL COMMENT '生日',
    `role` VARCHAR(20) DEFAULT 'user' COMMENT '角色: admin/user',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `status` TINYINT DEFAULT 0 COMMENT '状态: 0正常 1禁用',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0未删除 1已删除',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_email` (`email`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- =============================================
-- 2. AQI数据表 (aqi_data)
-- =============================================
DROP TABLE IF EXISTS `aqi_data`;
CREATE TABLE `aqi_data` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `city` VARCHAR(50) NOT NULL COMMENT '城市名',
    `aqi_value` INT NOT NULL COMMENT 'AQI数值',
    `quality_level` VARCHAR(20) NOT NULL COMMENT '空气质量等级: 优/良/轻度污染/中度污染/重度污染/严重污染',
    `date` DATE NOT NULL COMMENT '日期',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_city_date` (`city`, `date`),
    KEY `idx_city` (`city`),
    KEY `idx_date` (`date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AQI数据表';

-- =============================================
-- 3. 污染物数据表 (pollutant_data)
-- =============================================
DROP TABLE IF EXISTS `pollutant_data`;
CREATE TABLE `pollutant_data` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `city` VARCHAR(50) NOT NULL COMMENT '城市名',
    `pollutant_type` VARCHAR(20) NOT NULL COMMENT '污染物类型: PM2.5/PM10/SO2/NO2/O3/CO',
    `value` DECIMAL(10,2) NOT NULL COMMENT '浓度值',
    `unit` VARCHAR(20) DEFAULT NULL COMMENT '单位',
    `date` DATE NOT NULL COMMENT '日期',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_city_date_type` (`city`, `date`, `pollutant_type`),
    KEY `idx_city` (`city`),
    KEY `idx_date` (`date`),
    KEY `idx_pollutant_type` (`pollutant_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='污染物数据表';

-- =============================================
-- 4. 污染源数据表 (pollution_source)
-- =============================================
DROP TABLE IF EXISTS `pollution_source`;
CREATE TABLE `pollution_source` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `city` VARCHAR(50) NOT NULL COMMENT '城市名',
    `source_type` VARCHAR(50) NOT NULL COMMENT '污染源类型: 工业排放/机动车尾气/扬尘/燃煤/其他',
    `contribution_rate` DECIMAL(5,2) NOT NULL COMMENT '贡献率(百分比)',
    `date` DATE NOT NULL COMMENT '日期',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_city_date_type` (`city`, `date`, `source_type`),
    KEY `idx_city` (`city`),
    KEY `idx_date` (`date`),
    KEY `idx_source_type` (`source_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='污染源数据表';

-- =============================================
-- 5. 文章表 (article)
-- =============================================
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title` VARCHAR(200) NOT NULL COMMENT '标题',
    `content` TEXT COMMENT '内容(富文本)',
    `type` VARCHAR(20) NOT NULL COMMENT '类型: 政策/预警/科普',
    `status` TINYINT DEFAULT 0 COMMENT '状态: 0草稿 1发布',
    `is_top` TINYINT DEFAULT 0 COMMENT '是否置顶：0否 1是',
    `view_count` INT DEFAULT 0 COMMENT '阅读量',
    `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '封面图片URL',
    `summary` VARCHAR(500) DEFAULT NULL COMMENT '摘要',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0未删除 1已删除',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_type` (`type`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章表';

-- =============================================
-- 插入示例数据
-- =============================================

-- 插入用户数据 (密码都是 123456，使用MD5加密)
-- MD5('123456') = e10adc3949ba59abbe56e057f20f883e
INSERT INTO `user` (`username`, `password`, `email`, `birthday`, `role`, `status`) VALUES
('admin', 'e10adc3949ba59abbe56e057f20f883e', 'admin@airquality.com', '1990-01-15', 'admin', 0),
('zhangsan', 'e10adc3949ba59abbe56e057f20f883e', 'zhangsan@example.com', '1995-05-20', 'user', 0),
('lisi', 'e10adc3949ba59abbe56e057f20f883e', 'lisi@example.com', '1998-08-10', 'user', 0);

-- 插入AQI数据 (北京、上海、广州 2024年1月数据)
INSERT INTO `aqi_data` (`city`, `aqi_value`, `quality_level`, `date`) VALUES
-- 北京
('北京', 156, '中度污染', '2024-01-01'),
('北京', 89, '良', '2024-01-02'),
('北京', 203, '重度污染', '2024-01-03'),
('北京', 45, '优', '2024-01-04'),
('北京', 112, '轻度污染', '2024-01-05'),
('北京', 78, '良', '2024-01-06'),
('北京', 134, '轻度污染', '2024-01-07'),
-- 上海
('上海', 67, '良', '2024-01-01'),
('上海', 52, '良', '2024-01-02'),
('上海', 38, '优', '2024-01-03'),
('上海', 95, '良', '2024-01-04'),
('上海', 121, '轻度污染', '2024-01-05'),
('上海', 58, '良', '2024-01-06'),
('上海', 42, '优', '2024-01-07'),
-- 广州
('广州', 48, '优', '2024-01-01'),
('广州', 62, '良', '2024-01-02'),
('广州', 35, '优', '2024-01-03'),
('广州', 78, '良', '2024-01-04'),
('广州', 56, '良', '2024-01-05'),
('广州', 41, '优', '2024-01-06'),
('广州', 69, '良', '2024-01-07');

-- 插入污染物数据
INSERT INTO `pollutant_data` (`city`, `pollutant_type`, `value`, `unit`, `date`) VALUES
-- 北京 2024-01-01
('北京', 'PM2.5', 118.5, 'μg/m³', '2024-01-01'),
('北京', 'PM10', 185.2, 'μg/m³', '2024-01-01'),
('北京', 'SO2', 12.3, 'μg/m³', '2024-01-01'),
('北京', 'NO2', 68.5, 'μg/m³', '2024-01-01'),
('北京', 'O3', 45.2, 'μg/m³', '2024-01-01'),
('北京', 'CO', 1.2, 'mg/m³', '2024-01-01'),
-- 上海 2024-01-01
('上海', 'PM2.5', 45.8, 'μg/m³', '2024-01-01'),
('上海', 'PM10', 72.3, 'μg/m³', '2024-01-01'),
('上海', 'SO2', 8.5, 'μg/m³', '2024-01-01'),
('上海', 'NO2', 42.1, 'μg/m³', '2024-01-01'),
('上海', 'O3', 58.6, 'μg/m³', '2024-01-01'),
('上海', 'CO', 0.8, 'mg/m³', '2024-01-01'),
-- 广州 2024-01-01
('广州', 'PM2.5', 32.5, 'μg/m³', '2024-01-01'),
('广州', 'PM10', 48.6, 'μg/m³', '2024-01-01'),
('广州', 'SO2', 6.2, 'μg/m³', '2024-01-01'),
('广州', 'NO2', 35.8, 'μg/m³', '2024-01-01'),
('广州', 'O3', 52.3, 'μg/m³', '2024-01-01'),
('广州', 'CO', 0.6, 'mg/m³', '2024-01-01');

-- 插入污染源数据
INSERT INTO `pollution_source` (`city`, `source_type`, `contribution_rate`, `date`) VALUES
-- 北京
('北京', '工业排放', 28.5, '2024-01-01'),
('北京', '机动车尾气', 35.2, '2024-01-01'),
('北京', '扬尘', 18.6, '2024-01-01'),
('北京', '燃煤', 12.3, '2024-01-01'),
('北京', '其他', 5.4, '2024-01-01'),
-- 上海
('上海', '工业排放', 22.8, '2024-01-01'),
('上海', '机动车尾气', 42.5, '2024-01-01'),
('上海', '扬尘', 15.3, '2024-01-01'),
('上海', '燃煤', 8.6, '2024-01-01'),
('上海', '其他', 10.8, '2024-01-01'),
-- 广州
('广州', '工业排放', 18.5, '2024-01-01'),
('广州', '机动车尾气', 45.2, '2024-01-01'),
('广州', '扬尘', 12.8, '2024-01-01'),
('广州', '燃煤', 6.5, '2024-01-01'),
('广州', '其他', 17.0, '2024-01-01');

-- 插入文章数据
INSERT INTO `article` (`title`, `content`, `type`, `status`, `is_top`, `view_count`, `summary`) VALUES
('《大气污染防治法》最新修订解读', '<h2>修订背景</h2><p>为加强大气污染防治，改善环境质量...</p>', '政策', 1, 0, 156, '为加强大气污染防治，改善环境质量...'),
('北京市发布重污染天气橙色预警', '<h2>预警信息</h2><p>根据空气质量预报，1月3日北京市将出现重度污染天气...</p>', '预警', 1, 1, 328, '根据空气质量预报，1月3日北京市将出现重度污染天气...'),
('PM2.5对人体健康的影响', '<h2>什么是PM2.5</h2><p>PM2.5是指空气动力学直径小于或等于2.5微米的颗粒物...</p>', '科普', 1, 0, 89, 'PM2.5是指空气动力学直径小于或等于2.5微米的颗粒物...'),
('如何正确选择和使用空气净化器', '<h2>选购指南</h2><p>选择空气净化器时应注意以下几点...</p>', '科普', 1, 0, 245, '选择空气净化器时应注意以下几点...'),
('长三角区域大气污染联防联控机制', '<h2>机制介绍</h2><p>长三角区域建立了大气污染联防联控机制...</p>', '政策', 1, 0, 67, '长三角区域建立了大气污染联防联控机制...');

-- =============================================
-- 查询验证
-- =============================================
SELECT '用户数据:' AS '表';
SELECT * FROM `user`;
SELECT 'AQI数据:' AS '表';
SELECT * FROM `aqi_data`;
SELECT '污染物数据:' AS '表';
SELECT * FROM `pollutant_data`;
SELECT '污染源数据:' AS '表';
SELECT * FROM `pollution_source`;
SELECT '文章数据:' AS '表';
SELECT * FROM `article`;

-- =============================================
-- 如果数据库已存在，执行以下语句清理重复数据并添加唯一索引
-- =============================================

-- 清理aqi_data重复数据（保留id最小的记录）
-- DELETE t1 FROM aqi_data t1
-- INNER JOIN aqi_data t2
-- WHERE t1.id > t2.id AND t1.city = t2.city AND t1.date = t2.date;

-- 为aqi_data添加唯一索引（如果不存在）
-- ALTER TABLE aqi_data ADD UNIQUE INDEX uk_city_date (city, date);

-- 清理pollutant_data重复数据
-- DELETE t1 FROM pollutant_data t1
-- INNER JOIN pollutant_data t2
-- WHERE t1.id > t2.id AND t1.city = t2.city AND t1.date = t2.date AND t1.pollutant_type = t2.pollutant_type;

-- 为pollutant_data添加唯一索引（如果不存在）
-- ALTER TABLE pollutant_data ADD UNIQUE INDEX uk_city_date_type (city, date, pollutant_type);

-- 清理pollution_source重复数据
-- DELETE t1 FROM pollution_source t1
-- INNER JOIN pollution_source t2
-- WHERE t1.id > t2.id AND t1.city = t2.city AND t1.date = t2.date AND t1.source_type = t2.source_type;

-- 为pollution_source添加唯一索引（如果不存在）
-- ALTER TABLE pollution_source ADD UNIQUE INDEX uk_city_date_type (city, date, source_type);
