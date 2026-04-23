package com.airquality.service;

import com.airquality.dto.*;
import com.airquality.entity.AqiData;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * AQI服务接口
 */
public interface AqiService {

    /**
     * 条件查询AQI数据
     */
    List<AqiData> searchAqi(AqiSearchDTO dto);

    /**
     * 获取AQI排名
     */
    List<AqiRankingDTO> getRanking(int limit);

    /**
     * 获取某城市AQI趋势
     */
    List<AqiTrendDTO> getTrend(String city, int days);

    /**
     * 获取空气质量等级分布（饼图数据）
     */
    List<AqiPieDTO> getPieData(String city, String startDate, String endDate);

    /**
     * 多城市对比
     */
    List<AqiRankingDTO> getMultiCityCompare(List<String> cities, String date);

    /**
     * 导出AQI数据到Excel
     */
    void exportAqi(AqiSearchDTO dto, HttpServletResponse response);

    /**
     * 获取首页统计数据
     */
    HomeStatsDTO getHomeStats(String city);

    /**
     * 获取最新日期各城市AQI数据
     */
    List<AqiRankingDTO> getLatestCityAqi();

    /**
     * 获取最新日期各城市AQI数据（带坐标）
     */
    List<CityAqiDTO> getLatestCityAqiWithCoords();

    /**
     * 获取所有城市列表
     */
    List<String> getAllCities();

    /**
     * 获取空气质量预警数据
     */
    List<AqiRankingDTO> getAqiAlerts(String city);

    /**
     * 获取历史同比数据
     */
    List<AqiTrendDTO> getYearCompare(String city, int year);

    /**
     * 导出CSV格式
     */
    void exportAqiCsv(AqiSearchDTO dto, HttpServletResponse response);
}
