package com.airquality.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * AQI管理查询DTO
 */
@Data
public class AqiManageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 城市
     */
    private String city;

    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;

    /**
     * 当前页
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;
}
