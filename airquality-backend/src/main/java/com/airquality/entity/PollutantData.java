package com.airquality.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 污染物数据实体类
 */
@Data
@TableName("pollutant_data")
public class PollutantData implements Serializable {

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
     * 污染物类型（PM2.5、PM10、SO2、NO2、O3、CO）
     */
    private String pollutantType;

    /**
     * 浓度值
     */
    @TableField("value")
    private BigDecimal concentration;

    /**
     * 单位（μg/m³ 或 mg/m³）
     */
    private String unit;

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
