package com.airquality.service.impl;

import com.airquality.dto.*;
import com.airquality.entity.AqiData;
import com.airquality.mapper.AqiMapper;
import com.airquality.service.AqiService;
import com.airquality.vo.AqiExportVO;
import com.alibaba.excel.EasyExcel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AQI服务实现类
 */
@Slf4j
@Service
public class AqiServiceImpl implements AqiService {

    @Resource
    private AqiMapper aqiMapper;

    @Override
    public List<AqiData> searchAqi(AqiSearchDTO dto) {
        return aqiMapper.searchAqi(dto);
    }

    @Override
    public List<AqiRankingDTO> getRanking(int limit) {
        return aqiMapper.getAqiRanking(limit);
    }

    @Override
    public List<AqiTrendDTO> getTrend(String city, int days) {
        return aqiMapper.getTrendByCity(city, days);
    }

    @Override
    public List<AqiPieDTO> getPieData(String city, String startDate, String endDate) {
        return aqiMapper.getQualityDistribution(city, startDate, endDate);
    }

    @Override
    public List<AqiRankingDTO> getMultiCityCompare(List<String> cities, String date) {
        // 如果没有传入日期，使用最新日期
        if (date == null || date.isEmpty()) {
            date = aqiMapper.getLatestDate();
        }
        return aqiMapper.getMultiCityCompare(cities, date);
    }

    @Override
    public void exportAqi(AqiSearchDTO dto, HttpServletResponse response) {
        // 查询数据
        List<AqiData> dataList = aqiMapper.searchAqi(dto);

        // 转换为导出VO
        List<AqiExportVO> exportList = dataList.stream().map(aqi -> {
            AqiExportVO vo = new AqiExportVO();
            BeanUtils.copyProperties(aqi, vo);
            vo.setDate(aqi.getDate() != null ? aqi.getDate().toString() : null);
            return vo;
        }).collect(Collectors.toList());

        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("AQI数据导出");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

        // 使用EasyExcel导出
        try {
            EasyExcel.write(response.getOutputStream(), AqiExportVO.class)
                    .sheet("AQI数据")
                    .doWrite(exportList);
        } catch (IOException e) {
            log.error("导出Excel失败", e);
            throw new RuntimeException("导出Excel失败");
        }
    }

    @Override
    public HomeStatsDTO getHomeStats(String city) {
        HomeStatsDTO stats = new HomeStatsDTO();

        Integer currentAqi;
        Integer yesterdayAqi;

        if (city != null && !city.isEmpty()) {
            // 获取指定城市的AQI
            currentAqi = aqiMapper.getLatestAqiByCity(city);
            yesterdayAqi = aqiMapper.getYesterdayAqiByCity(city);
        } else {
            // 获取所有城市的平均AQI
            Double avgAqi = aqiMapper.getLatestAvgAqi();
            currentAqi = avgAqi != null ? avgAqi.intValue() : 0;
            Double avgYesterday = aqiMapper.getYesterdayAvgAqi();
            yesterdayAqi = avgYesterday != null ? avgYesterday.intValue() : null;
        }

        stats.setCurrentAqi(currentAqi != null ? currentAqi : 0);

        // 获取最新日期
        String latestDate = aqiMapper.getLatestDate();
        stats.setLatestDate(latestDate);

        // 计算环比
        if (currentAqi != null && yesterdayAqi != null) {
            stats.setCompareValue(currentAqi - yesterdayAqi);
        } else {
            stats.setCompareValue(0);
        }

        // 计算质量等级
        stats.setQualityLevel(calculateQualityLevel(stats.getCurrentAqi()));

        // 获取城市数量和记录总数
        stats.setCityCount(aqiMapper.getCityCount());
        stats.setTotalCount(aqiMapper.getTotalCount());

        return stats;
    }

    @Override
    public List<AqiRankingDTO> getLatestCityAqi() {
        return aqiMapper.getLatestCityAqi();
    }

    @Override
    public List<CityAqiDTO> getLatestCityAqiWithCoords() {
        return aqiMapper.getLatestCityAqiWithCoords();
    }

    @Override
    public List<String> getAllCities() {
        return aqiMapper.getAllCities();
    }

    @Override
    public List<AqiRankingDTO> getAqiAlerts(String city) {
        // 获取AQI > 150 的城市作为预警数据
        return aqiMapper.getAqiAlerts(city);
    }

    @Override
    public List<AqiTrendDTO> getYearCompare(String city, int year) {
        // 获取指定年份的月度平均数据
        return aqiMapper.getYearCompare(city, year);
    }

    @Override
    public void exportAqiCsv(AqiSearchDTO dto, HttpServletResponse response) {
        // 查询数据
        List<AqiData> dataList = aqiMapper.searchAqi(dto);

        // 设置响应头
        response.setContentType("text/csv");
        response.setCharacterEncoding("utf-8");
        String fileName;
        try {
            fileName = URLEncoder.encode("AQI数据导出", "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            fileName = "AQI_data";
        }
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".csv");

        // 写入CSV
        try {
            java.io.PrintWriter writer = response.getWriter();
            // 写入BOM以支持中文
            writer.write('\ufeff');
            // 写入表头
            writer.println("城市,日期,AQI,质量等级");
            // 写入数据
            for (AqiData aqi : dataList) {
                writer.println(aqi.getCity() + "," + aqi.getDate() + "," + aqi.getAqiValue() + "," + aqi.getQualityLevel());
            }
            writer.flush();
        } catch (IOException e) {
            log.error("导出CSV失败", e);
            throw new RuntimeException("导出CSV失败");
        }
    }

    /**
     * 根据AQI值计算质量等级
     */
    private String calculateQualityLevel(Integer aqiValue) {
        if (aqiValue == null) return "未知";
        if (aqiValue <= 50) return "优";
        if (aqiValue <= 100) return "良";
        if (aqiValue <= 150) return "轻度污染";
        if (aqiValue <= 200) return "中度污染";
        if (aqiValue <= 300) return "重度污染";
        return "严重污染";
    }
}
