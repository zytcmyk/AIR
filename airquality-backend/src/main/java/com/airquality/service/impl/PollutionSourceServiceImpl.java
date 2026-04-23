package com.airquality.service.impl;

import com.airquality.dto.CityRankingDTO;
import com.airquality.dto.SourceDistributionDTO;
import com.airquality.dto.SourceSearchDTO;
import com.airquality.dto.SourceTrendDTO;
import com.airquality.entity.PollutionSource;
import com.airquality.mapper.PollutionSourceMapper;
import com.airquality.service.PollutionSourceService;
import com.airquality.vo.SourceExportVO;
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
 * 污染源服务实现类
 */
@Slf4j
@Service
public class PollutionSourceServiceImpl implements PollutionSourceService {

    @Resource
    private PollutionSourceMapper pollutionSourceMapper;

    @Override
    public List<PollutionSource> searchSource(SourceSearchDTO dto) {
        return pollutionSourceMapper.searchSource(dto);
    }

    @Override
    public Map<String, Object> getCitySourcePie(String city, String startDate, String endDate) {
        List<SourceDistributionDTO> distribution = pollutionSourceMapper.getSourceDistributionByCity(city, startDate, endDate);

        Map<String, Object> result = new HashMap<>();
        result.put("city", city);
        result.put("data", distribution);

        // 计算总贡献率
        BigDecimal total = distribution.stream()
                .map(SourceDistributionDTO::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 添加预警信息
        List<Map<String, Object>> warnings = new ArrayList<>();
        for (SourceDistributionDTO item : distribution) {
            if (item.getValue().compareTo(new BigDecimal("40")) > 0) {
                Map<String, Object> warning = new HashMap<>();
                warning.put("sourceType", item.getName());
                warning.put("contributionRate", item.getValue());
                warning.put("message", item.getName() + "贡献率偏高，建议加强管控");
                warnings.add(warning);
            }
        }
        result.put("warnings", warnings);
        result.put("total", total);

        return result;
    }

    @Override
    public Map<String, Object> getOverallSourcePie(String startDate, String endDate) {
        List<SourceDistributionDTO> distribution = pollutionSourceMapper.getOverallSourceDistribution(startDate, endDate);

        Map<String, Object> result = new HashMap<>();
        result.put("data", distribution);

        // 计算总贡献率
        BigDecimal total = distribution.stream()
                .map(SourceDistributionDTO::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        result.put("total", total);
        return result;
    }

    @Override
    public List<SourceTrendDTO> getSourceTrend(String city, String sourceType, Integer days) {
        // 默认值处理
        if (city == null || city.isEmpty()) {
            city = "北京";
        }
        if (sourceType == null || sourceType.isEmpty()) {
            sourceType = "工业排放";
        }
        if (days == null || days <= 0) {
            days = 30;
        }
        return pollutionSourceMapper.getSourceTrendByCityAndType(city, sourceType, days);
    }

    @Override
    public List<Map<String, Object>> getMultiCitySourceCompare(List<String> cities, String sourceType, String date) {
        return pollutionSourceMapper.getMultiCitySourceCompare(cities, sourceType, date);
    }

    @Override
    public void exportSource(SourceSearchDTO dto, HttpServletResponse response) {
        // 查询数据
        List<PollutionSource> dataList = pollutionSourceMapper.searchSource(dto);

        // 转换为导出VO
        List<SourceExportVO> exportList = dataList.stream().map(s -> {
            SourceExportVO vo = new SourceExportVO();
            BeanUtils.copyProperties(s, vo);
            vo.setDate(s.getDate() != null ? s.getDate().toString() : null);
            vo.setContributionRate(s.getContributionRate() != null
                    ? s.getContributionRate().setScale(2, RoundingMode.HALF_UP).toString() + "%"
                    : null);
            return vo;
        }).collect(Collectors.toList());

        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("污染源数据导出");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

        // 使用EasyExcel导出
        try {
            EasyExcel.write(response.getOutputStream(), SourceExportVO.class)
                    .sheet("污染源数据")
                    .doWrite(exportList);
        } catch (IOException e) {
            log.error("导出Excel失败", e);
            throw new RuntimeException("导出Excel失败");
        }
    }

    @Override
    public List<PollutionSource> getSourceRanking(int limit, String city) {
        return pollutionSourceMapper.getSourceRanking(limit, city);
    }

    @Override
    public List<Map<String, Object>> getTrendByPeriod(String city, String period, String startDate, String endDate) {
        // 默认值处理
        if (period == null || period.isEmpty()) {
            period = "month";
        }
        return pollutionSourceMapper.getTrendByPeriod(city, period, startDate, endDate);
    }

    @Override
    public List<CityRankingDTO> getCityRankingBySource(int limit, String sourceType, String startDate, String endDate) {
        // 默认值处理
        if (limit <= 0) {
            limit = 5;
        }
        List<CityRankingDTO> rankingList = pollutionSourceMapper.getCityRankingBySource(limit, sourceType, startDate, endDate);

        // 设置排名
        int rank = 1;
        for (CityRankingDTO dto : rankingList) {
            dto.setRank(rank++);
        }

        return rankingList;
    }

    @Override
    public List<Map<String, Object>> getCitySourceStructureCompare(List<String> cities, String startDate, String endDate) {
        if (cities == null || cities.isEmpty()) {
            cities = Arrays.asList("北京", "上海", "广州", "成都");
        }
        return pollutionSourceMapper.getCitySourceStructureCompare(cities, startDate, endDate);
    }
}
