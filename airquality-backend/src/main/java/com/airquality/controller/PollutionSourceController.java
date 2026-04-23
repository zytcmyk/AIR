package com.airquality.controller;

import com.airquality.dto.CityRankingDTO;
import com.airquality.dto.SourceSearchDTO;
import com.airquality.dto.SourceTrendDTO;
import com.airquality.entity.PollutionSource;
import com.airquality.service.PollutionSourceService;
import com.airquality.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 污染源分析控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/source")
public class PollutionSourceController {

    @Resource
    private PollutionSourceService pollutionSourceService;

    /**
     * 条件查询污染源数据
     * GET /api/source/search
     */
    @GetMapping("/search")
    public Result<List<PollutionSource>> search(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String sourceType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        SourceSearchDTO dto = new SourceSearchDTO();
        dto.setCity(city);
        dto.setSourceType(sourceType);
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);
        List<PollutionSource> list = pollutionSourceService.searchSource(dto);
        return Result.success(list);
    }

    /**
     * 获取某城市污染源占比（饼图数据）
     * GET /api/source/city-pie
     */
    @GetMapping("/city-pie")
    public Result<Map<String, Object>> getCitySourcePie(
            @RequestParam(defaultValue = "北京") String city,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Map<String, Object> data = pollutionSourceService.getCitySourcePie(city, startDate, endDate);
        return Result.success(data);
    }

    /**
     * 获取整体污染源分布（饼图数据）
     * GET /api/source/overall-pie
     */
    @GetMapping("/overall-pie")
    public Result<Map<String, Object>> getOverallSourcePie(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Map<String, Object> data = pollutionSourceService.getOverallSourcePie(startDate, endDate);
        return Result.success(data);
    }

    /**
     * 获取某城市某污染源趋势
     * GET /api/source/trend
     */
    @GetMapping("/trend")
    public Result<List<SourceTrendDTO>> getSourceTrend(
            @RequestParam(defaultValue = "北京") String city,
            @RequestParam(defaultValue = "工业排放") String sourceType,
            @RequestParam(defaultValue = "30") Integer days) {
        List<SourceTrendDTO> list = pollutionSourceService.getSourceTrend(city, sourceType, days);
        return Result.success(list);
    }

    /**
     * 获取多城市某污染源贡献率对比
     * GET /api/source/compare
     */
    @GetMapping("/compare")
    public Result<List<Map<String, Object>>> getMultiCitySourceCompare(
            @RequestParam String cities,
            @RequestParam String sourceType,
            @RequestParam String date) {
        List<String> cityList = Arrays.asList(cities.split(","));
        List<Map<String, Object>> list = pollutionSourceService.getMultiCitySourceCompare(cityList, sourceType, date);
        return Result.success(list);
    }

    /**
     * 导出污染源数据到Excel
     * POST /api/source/export
     */
    @PostMapping("/export")
    public void exportSource(@RequestBody SourceSearchDTO dto, HttpServletResponse response) {
        pollutionSourceService.exportSource(dto, response);
    }

    /**
     * 获取污染源排名
     * GET /api/source/ranking
     */
    @GetMapping("/ranking")
    public Result<List<PollutionSource>> getSourceRanking(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String city) {
        List<PollutionSource> list = pollutionSourceService.getSourceRanking(limit, city);
        return Result.success(list);
    }

    /**
     * 获取某城市污染源贡献分布（饼图数据）
     * GET /api/source/distribution
     */
    @GetMapping("/distribution")
    public Result<Map<String, Object>> getDistribution(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Map<String, Object> data = pollutionSourceService.getCitySourcePie(city, startDate, endDate);
        return Result.success(data);
    }

    /**
     * 获取按周期聚合的趋势数据
     * GET /api/source/trend-by-period
     */
    @GetMapping("/trend-by-period")
    public Result<List<Map<String, Object>>> getTrendByPeriod(
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "month") String period,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        List<Map<String, Object>> list = pollutionSourceService.getTrendByPeriod(city, period, startDate, endDate);
        return Result.success(list);
    }

    /**
     * 获取城市污染贡献排名
     * GET /api/source/ranking-by-source
     */
    @GetMapping("/ranking-by-source")
    public Result<List<CityRankingDTO>> getCityRankingBySource(
            @RequestParam(defaultValue = "5") Integer limit,
            @RequestParam(required = false) String sourceType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        List<CityRankingDTO> list = pollutionSourceService.getCityRankingBySource(limit, sourceType, startDate, endDate);
        return Result.success(list);
    }

    /**
     * 获取多城市污染源结构对比数据
     * GET /api/source/city-structure-compare
     */
    @GetMapping("/city-structure-compare")
    public Result<List<Map<String, Object>>> getCitySourceStructureCompare(
            @RequestParam(required = false) String cities,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        List<String> cityList = null;
        if (cities != null && !cities.isEmpty()) {
            cityList = Arrays.asList(cities.split(","));
        }
        List<Map<String, Object>> list = pollutionSourceService.getCitySourceStructureCompare(cityList, startDate, endDate);
        return Result.success(list);
    }
}
