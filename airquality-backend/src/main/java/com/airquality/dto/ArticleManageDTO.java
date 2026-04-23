package com.airquality.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 文章管理查询DTO
 */
@Data
public class ArticleManageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;
    private String type;
    private Integer status;
    private Integer page = 1;
    private Integer size = 10;
}
