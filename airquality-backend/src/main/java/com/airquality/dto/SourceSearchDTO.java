package com.airquality.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 污染源搜索条件DTO
 */
@Data
public class SourceSearchDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 城市名
     */
    private String city;

    /**
     * 污染源类型（工业排放、机动车尾气、扬尘、燃煤、其他）
     */
    private String sourceType;

    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;
}
