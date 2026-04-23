package com.airquality.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户列表查询DTO
 */
@Data
public class UserListDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 状态：0正常 1禁用
     */
    private Integer status;

    /**
     * 当前页
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;
}
