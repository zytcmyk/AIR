-- 为污染物数据表添加索引，优化查询性能
-- 如果表数据量很大，建议在数据库中执行此脚本

-- 1. 为常用查询字段添加索引
CREATE INDEX IF NOT EXISTS idx_pollutant_city ON pollutant_data(city);
CREATE INDEX IF NOT EXISTS idx_pollutant_type ON pollutant_data(pollutant_type);
CREATE INDEX IF NOT EXISTS idx_pollutant_date ON pollutant_data(date);
CREATE INDEX IF NOT EXISTS idx_pollutant_city_type ON pollutant_data(city, pollutant_type);
CREATE INDEX IF NOT EXISTS idx_pollutant_city_date ON pollutant_data(city, date);

-- 2. 为AQI数据表添加索引
CREATE INDEX IF NOT EXISTS idx_aqi_city ON aqi_data(city);
CREATE INDEX IF NOT EXISTS idx_aqi_date ON aqi_data(date);
CREATE INDEX IF NOT EXISTS idx_aqi_city_date ON aqi_data(city, date);
CREATE INDEX IF NOT EXISTS idx_aqi_value ON aqi_data(aqi_value);

-- 3. 为污染源数据表添加索引
CREATE INDEX IF NOT EXISTS idx_source_city ON pollution_source(city);
CREATE INDEX IF NOT EXISTS idx_source_type ON pollution_source(source_type);
CREATE INDEX IF NOT EXISTS idx_source_date ON pollution_source(date);
CREATE INDEX IF NOT EXISTS idx_source_city_type ON pollution_source(city, source_type);

-- 4. 组合索引（用于常见查询模式）
CREATE INDEX IF NOT EXISTS idx_pollutant_composite ON pollutant_data(city, pollutant_type, date);
CREATE INDEX IF NOT EXISTS idx_aqi_composite ON aqi_data(city, date, aqi_value);
