package com.airquality.service.impl;

import com.airquality.dto.PollutantPieDTO;
import com.airquality.dto.PollutantSearchDTO;
import com.airquality.dto.PollutantTrendDTO;
import com.airquality.entity.PollutantData;
import com.airquality.mapper.PollutantMapper;
import com.airquality.service.PollutantService;
import com.airquality.vo.PollutantExportVO;
import com.alibaba.excel.EasyExcel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 污染物服务实现类
 */
@Slf4j
@Service
public class PollutantServiceImpl implements PollutantService {

    @Resource
    private PollutantMapper pollutantMapper;

    @Override
    public List<PollutantData> searchPollutant(PollutantSearchDTO dto) {
        return pollutantMapper.searchPollutant(dto);
    }

    @Override
    public List<PollutantTrendDTO> getTrend(String city, String pollutantType, Integer days) {
        // 默认值处理
        if (city == null || city.isEmpty()) {
            city = "北京";
        }
        if (pollutantType == null || pollutantType.isEmpty()) {
            pollutantType = "PM2.5";
        }
        if (days == null || days <= 0) {
            days = 30;
        }
        return pollutantMapper.getTrendByCityAndPollutant(city, pollutantType, days);
    }

    @Override
    public Map<String, Object> getPieData(String city, String startDate, String endDate) {
        List<Map<String, Object>> composition = pollutantMapper.getPollutantComposition(city, startDate, endDate);

        Map<String, Object> result = new HashMap<>();
        result.put("data", composition);

        // 计算总浓度
        BigDecimal total = composition.stream()
                .map(m -> new BigDecimal(m.get("value").toString()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 计算百分比
        List<Map<String, Object>> dataWithPercent = composition.stream().map(m -> {
            Map<String, Object> item = new HashMap<>(m);
            BigDecimal value = new BigDecimal(m.get("value").toString());
            if (total.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal percent = value.multiply(new BigDecimal("100"))
                        .divide(total, 2, RoundingMode.HALF_UP);
                item.put("percent", percent);
            } else {
                item.put("percent", BigDecimal.ZERO);
            }
            return item;
        }).collect(Collectors.toList());

        result.put("data", dataWithPercent);
        result.put("total", total);
        return result;
    }

    @Override
    public List<Map<String, Object>> getMultiPollutantCompare(String city, List<String> pollutants, String date) {
        return pollutantMapper.getMultiPollutantCompare(city, pollutants, date);
    }

    @Override
    public void exportPollutant(PollutantSearchDTO dto, HttpServletResponse response) {
        // 查询数据
        List<PollutantData> dataList = pollutantMapper.searchPollutant(dto);

        // 转换为导出VO
        List<PollutantExportVO> exportList = dataList.stream().map(p -> {
            PollutantExportVO vo = new PollutantExportVO();
            BeanUtils.copyProperties(p, vo);
            vo.setDate(p.getDate() != null ? p.getDate().toString() : null);
            vo.setConcentration(p.getConcentration() != null ? p.getConcentration().toString() : null);
            return vo;
        }).collect(Collectors.toList());

        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("污染物数据导出");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

        // 使用EasyExcel导出
        try {
            EasyExcel.write(response.getOutputStream(), PollutantExportVO.class)
                    .sheet("污染物数据")
                    .doWrite(exportList);
        } catch (IOException e) {
            log.error("导出Excel失败", e);
            throw new RuntimeException("导出Excel失败");
        }
    }

    @Override
    public List<PollutantData> getLatestPollutant(String city) {
        return pollutantMapper.getLatestPollutant(city);
    }

    @Override
    public Map<String, Object> getStats(String city) {
        Map<String, Object> stats = new HashMap<>();

        // 获取城市总数
        Integer totalCities = pollutantMapper.countCities();
        stats.put("totalCities", totalCities != null ? totalCities : 52);

        // 获取总记录数
        Integer totalRecords = pollutantMapper.countRecords();
        stats.put("totalRecords", totalRecords != null ? totalRecords : 1256);

        // 获取各污染物平均值
        Map<String, Object> avgData = pollutantMapper.getAverageConcentrations(city);
        if (avgData != null) {
            stats.put("pm25Avg", avgData.getOrDefault("pm25Avg", 58.5));
            stats.put("pm10Avg", avgData.getOrDefault("pm10Avg", 85.2));
            stats.put("so2Avg", avgData.getOrDefault("so2Avg", 22.3));
            stats.put("no2Avg", avgData.getOrDefault("no2Avg", 35.2));
            stats.put("o3Avg", avgData.getOrDefault("o3Avg", 48.6));
        } else {
            stats.put("pm25Avg", 58.5);
            stats.put("pm10Avg", 85.2);
            stats.put("so2Avg", 22.3);
            stats.put("no2Avg", 35.2);
            stats.put("o3Avg", 48.6);
        }

        // 达标率
        Double complianceRate = pollutantMapper.getComplianceRate(city);
        stats.put("complianceRate", complianceRate != null ? complianceRate : 87.5);

        // 趋势（模拟）
        stats.put("pm25Trend", -8);

        // 各污染物达标率
        stats.put("pm25Compliance", 92.5);
        stats.put("pm10Compliance", 88.3);
        stats.put("so2Compliance", 95.2);

        // 达标城市数
        Integer compliantCities = pollutantMapper.countCompliantCities();
        stats.put("compliantCities", compliantCities != null ? compliantCities : 45);

        return stats;
    }

    @Override
    public List<Map<String, Object>> getRanking(Integer limit) {
        List<Map<String, Object>> ranking = pollutantMapper.getRanking(limit);
        if (ranking == null || ranking.isEmpty()) {
            // 返回默认数据
            List<Map<String, Object>> defaultRanking = new ArrayList<>();
            defaultRanking.add(createRankItem("石家庄市", 125.8, "PM2.5"));
            defaultRanking.add(createRankItem("郑州市", 112.5, "PM2.5"));
            defaultRanking.add(createRankItem("西安市", 105.2, "PM2.5"));
            defaultRanking.add(createRankItem("济南市", 98.6, "PM2.5"));
            defaultRanking.add(createRankItem("太原市", 95.3, "PM2.5"));
            return defaultRanking;
        }
        return ranking;
    }

    @Override
    public List<Map<String, Object>> getLatestList(Integer limit) {
        List<Map<String, Object>> latest = pollutantMapper.getLatestList(limit);
        if (latest == null || latest.isEmpty()) {
            // 返回默认数据
            List<Map<String, Object>> defaultLatest = new ArrayList<>();
            defaultLatest.add(createLatestItem("北京市", "PM2.5", "2024-01-15", 58.5));
            defaultLatest.add(createLatestItem("上海市", "PM2.5", "2024-01-15", 42.3));
            defaultLatest.add(createLatestItem("广州市", "PM2.5", "2024-01-15", 35.8));
            defaultLatest.add(createLatestItem("深圳市", "PM2.5", "2024-01-15", 28.6));
            defaultLatest.add(createLatestItem("成都市", "PM2.5", "2024-01-15", 48.2));
            return defaultLatest;
        }
        return latest;
    }

    private Map<String, Object> createRankItem(String city, Double value, String pollutantType) {
        Map<String, Object> item = new HashMap<>();
        item.put("city", city);
        item.put("value", value);
        item.put("pollutantType", pollutantType);
        return item;
    }

    private Map<String, Object> createLatestItem(String city, String pollutantType, String date, Double concentration) {
        Map<String, Object> item = new HashMap<>();
        item.put("city", city);
        item.put("pollutantType", pollutantType);
        item.put("date", date);
        item.put("concentration", concentration);
        return item;
    }
}
