package com.airquality.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 污染源数据实体类
 */
@Data
@TableName("pollution_source")
public class PollutionSource implements Serializable {

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
     * 污染源类型（工业排放、机动车尾气、扬尘、燃煤、其他）
     */
    private String sourceType;

    /**
     * 贡献率（百分比）
     */
    private BigDecimal contributionRate;

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
