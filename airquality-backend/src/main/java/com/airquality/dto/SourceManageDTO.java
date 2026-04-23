package com.airquality.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 污染源管理查询DTO
 */
@Data
public class SourceManageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String city;
    private String sourceType;
    private String startDate;
    private String endDate;
    private Integer page = 1;
    private Integer size = 10;
}
