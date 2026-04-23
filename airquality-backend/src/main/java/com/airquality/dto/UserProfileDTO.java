package com.airquality.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户个人信息DTO（返回给前端）
 */
@Data
public class UserProfileDTO {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 角色
     */
    private String role;

    /**
     * 状态：0正常 1禁用
     */
    private Integer status;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 注册时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
