package com.airquality.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * AQI数据实体类
 */
@Data
@TableName("aqi_data")
public class AqiData implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 城市名
     */
    private String city;

    /**
     * AQI数值
     */
    private Integer aqiValue;

    /**
     * 空气质量等级
     */
    private String qualityLevel;

    /**
     * 日期
     */
    private LocalDate date;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
