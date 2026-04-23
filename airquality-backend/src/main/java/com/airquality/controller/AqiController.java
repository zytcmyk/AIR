package com.airquality.controller;

import com.airquality.dto.*;
import com.airquality.entity.AqiData;
import com.airquality.service.AqiService;
import com.airquality.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * AQI分析控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/aqi")
public class AqiController {

    @Resource
    private AqiService aqiService;

    /**
     * 条件查询AQI数据
     * GET /api/aqi/search
     */
    @GetMapping("/search")
    public Result<List<AqiData>> search(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        AqiSearchDTO dto = new AqiSearchDTO();
        dto.setCity(city);
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);
        List<AqiData> list = aqiService.searchAqi(dto);
        return Result.success(list);
    }

    /**
     * 获取AQI排名
     * GET /api/aqi/ranking
     */
    @GetMapping("/ranking")
    public Result<List<AqiRankingDTO>> getRanking(
            @RequestParam(defaultValue = "10") int limit) {
        List<AqiRankingDTO> list = aqiService.getRanking(limit);
        return Result.success(list);
    }

    /**
     * 获取某城市AQI趋势
     * GET /api/aqi/trend
     */
    @GetMapping("/trend")
    public Result<List<AqiTrendDTO>> getTrend(
            @RequestParam(defaultValue = "北京") String city,
            @RequestParam(defaultValue = "30") int days) {
        List<AqiTrendDTO> list = aqiService.getTrend(city, days);
        return Result.success(list);
    }

    /**
     * 获取空气质量等级分布（饼图数据）
     * GET /api/aqi/pie
     */
    @GetMapping("/pie")
    public Result<List<AqiPieDTO>> getPieData(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        List<AqiPieDTO> list = aqiService.getPieData(city, startDate, endDate);
        return Result.success(list);
    }

    /**
     * 多城市对比
     * GET /api/aqi/compare
     */
    @GetMapping("/compare")
    public Result<List<AqiRankingDTO>> getMultiCityCompare(
            @RequestParam String cities,
            @RequestParam(required = false) String date) {
        List<String> cityList = Arrays.asList(cities.split(","));
        List<AqiRankingDTO> list = aqiService.getMultiCityCompare(cityList, date);
        return Result.success(list);
    }

    /**
     * 导出AQI数据到Excel
     * POST /api/aqi/export
     */
    @PostMapping("/export")
    public void exportAqi(@RequestBody AqiSearchDTO dto, HttpServletResponse response) {
        aqiService.exportAqi(dto, response);
    }

    /**
     * 获取首页统计数据
     * GET /api/aqi/home-stats
     */
    @GetMapping("/home-stats")
    public Result<HomeStatsDTO> getHomeStats(
            @RequestParam(required = false) String city) {
        HomeStatsDTO stats = aqiService.getHomeStats(city);
        return Result.success(stats);
    }

    /**
     * 获取最新日期各城市AQI数据
     * GET /api/aqi/latest-city
     */
    @GetMapping("/latest-city")
    public Result<List<AqiRankingDTO>> getLatestCityAqi() {
        List<AqiRankingDTO> list = aqiService.getLatestCityAqi();
        return Result.success(list);
    }

    /**
     * 获取最新日期各城市AQI数据（带坐标）
     * GET /api/aqi/latest-city-coords
     */
    @GetMapping("/latest-city-coords")
    public Result<List<CityAqiDTO>> getLatestCityAqiWithCoords() {
        List<CityAqiDTO> list = aqiService.getLatestCityAqiWithCoords();
        return Result.success(list);
    }

    /**
     * 获取所有城市列表
     * GET /api/aqi/cities
     */
    @GetMapping("/cities")
    public Result<List<String>> getAllCities() {
        List<String> list = aqiService.getAllCities();
        return Result.success(list);
    }

    /**
     * 获取空气质量预警数据
     * GET /api/aqi/alerts
     */
    @GetMapping("/alerts")
    public Result<List<AqiRankingDTO>> getAqiAlerts(
            @RequestParam(required = false) String city) {
        List<AqiRankingDTO> list = aqiService.getAqiAlerts(city);
        return Result.success(list);
    }

    /**
     * 获取历史同比数据
     * GET /api/aqi/year-compare
     */
    @GetMapping("/year-compare")
    public Result<List<AqiTrendDTO>> getYearCompare(
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "2022") int year) {
        List<AqiTrendDTO> list = aqiService.getYearCompare(city, year);
        return Result.success(list);
    }

    /**
     * 导出CSV格式
     * POST /api/aqi/export-csv
     */
    @PostMapping("/export-csv")
    public void exportAqiCsv(@RequestBody AqiSearchDTO dto, HttpServletResponse response) {
        aqiService.exportAqiCsv(dto, response);
    }
}
