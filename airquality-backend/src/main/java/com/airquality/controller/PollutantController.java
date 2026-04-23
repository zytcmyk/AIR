package com.airquality.controller;

import com.airquality.dto.PollutantSearchDTO;
import com.airquality.dto.PollutantTrendDTO;
import com.airquality.entity.PollutantData;
import com.airquality.service.PollutantService;
import com.airquality.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 污染物分析控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/pollutant")
public class PollutantController {

    @Resource
    private PollutantService pollutantService;

    /**
     * 条件查询污染物数据
     * GET /api/pollutant/search
     */
    @GetMapping("/search")
    public Result<List<PollutantData>> search(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String pollutantType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        PollutantSearchDTO dto = new PollutantSearchDTO();
        dto.setCity(city);
        dto.setPollutantType(pollutantType);
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);
        List<PollutantData> list = pollutantService.searchPollutant(dto);
        return Result.success(list);
    }

    /**
     * 获取某城市某污染物趋势
     * GET /api/pollutant/trend
     */
    @GetMapping("/trend")
    public Result<List<PollutantTrendDTO>> getTrend(
            @RequestParam(defaultValue = "北京") String city,
            @RequestParam(defaultValue = "PM2.5") String pollutantType,
            @RequestParam(defaultValue = "30") Integer days) {
        List<PollutantTrendDTO> list = pollutantService.getTrend(city, pollutantType, days);
        return Result.success(list);
    }

    /**
     * 获取某城市各污染物占比（饼图数据）
     * GET /api/pollutant/pie
     */
    @GetMapping("/pie")
    public Result<Map<String, Object>> getPieData(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Map<String, Object> data = pollutantService.getPieData(city, startDate, endDate);
        return Result.success(data);
    }

    /**
     * 获取某城市多个污染物浓度对比
     * GET /api/pollutant/compare
     */
    @GetMapping("/compare")
    public Result<List<Map<String, Object>>> getMultiPollutantCompare(
            @RequestParam String city,
            @RequestParam String pollutants,
            @RequestParam String date) {
        List<String> pollutantList = Arrays.asList(pollutants.split(","));
        List<Map<String, Object>> list = pollutantService.getMultiPollutantCompare(city, pollutantList, date);
        return Result.success(list);
    }

    /**
     * 导出污染物数据到Excel
     * POST /api/pollutant/export
     */
    @PostMapping("/export")
    public void exportPollutant(@RequestBody PollutantSearchDTO dto, HttpServletResponse response) {
        pollutantService.exportPollutant(dto, response);
    }

    /**
     * 获取最新污染物数据
     * GET /api/pollutant/latest
     */
    @GetMapping("/latest")
    public Result<List<PollutantData>> getLatestPollutant(
            @RequestParam(required = false) String city) {
        List<PollutantData> list = pollutantService.getLatestPollutant(city);
        return Result.success(list);
    }

    /**
     * 获取污染物统计数据
     * GET /api/pollutant/stats
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats(
            @RequestParam(required = false) String city) {
        Map<String, Object> data = pollutantService.getStats(city);
        return Result.success(data);
    }

    /**
     * 获取污染物浓度排名
     * GET /api/pollutant/ranking
     */
    @GetMapping("/ranking")
    public Result<List<Map<String, Object>>> getRanking(
            @RequestParam(defaultValue = "5") Integer limit) {
        List<Map<String, Object>> list = pollutantService.getRanking(limit);
        return Result.success(list);
    }

    /**
     * 获取最新污染物数据列表
     * GET /api/pollutant/latest-list
     */
    @GetMapping("/latest-list")
    public Result<List<Map<String, Object>>> getLatestList(
            @RequestParam(defaultValue = "5") Integer limit) {
        List<Map<String, Object>> list = pollutantService.getLatestList(limit);
        return Result.success(list);
    }
}
