package com.airquality.dto;

import lombok.Data;

/**
 * 用户统计数据DTO
 */
@Data
public class UserStatsDTO {

    /**
     * 登录次数
     */
    private Integer loginCount;

    /**
     * 数据查询次数
     */
    private Integer queryCount;

    /**
     * 收藏城市数量
     */
    private Integer favoriteCityCount;

    /**
     * 账号天数
     */
    private Integer accountDays;
}
