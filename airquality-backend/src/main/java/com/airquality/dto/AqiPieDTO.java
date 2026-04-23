package com.airquality.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * AQI饼图DTO
 */
@Data
public class AqiPieDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 名称（质量等级）
     */
    private String name;

    /**
     * 数值（数量）
     */
    private Integer value;
}
