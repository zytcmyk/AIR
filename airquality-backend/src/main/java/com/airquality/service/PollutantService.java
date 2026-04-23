package com.airquality.service;

import com.airquality.dto.PollutantPieDTO;
import com.airquality.dto.PollutantSearchDTO;
import com.airquality.dto.PollutantTrendDTO;
import com.airquality.entity.PollutantData;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 污染物服务接口
 */
public interface PollutantService {

    /**
     * 条件查询污染物数据
     */
    List<PollutantData> searchPollutant(PollutantSearchDTO dto);

    /**
     * 获取某城市某污染物趋势
     */
    List<PollutantTrendDTO> getTrend(String city, String pollutantType, Integer days);

    /**
     * 获取某城市各污染物占比（饼图数据）
     */
    Map<String, Object> getPieData(String city, String startDate, String endDate);

    /**
     * 获取某城市多个污染物浓度对比
     */
    List<Map<String, Object>> getMultiPollutantCompare(String city, List<String> pollutants, String date);

    /**
     * 导出污染物数据到Excel
     */
    void exportPollutant(PollutantSearchDTO dto, HttpServletResponse response);

    /**
     * 获取最新污染物数据
     */
    List<PollutantData> getLatestPollutant(String city);

    /**
     * 获取污染物统计数据
     */
    Map<String, Object> getStats(String city);

    /**
     * 获取污染物浓度排名
     */
    List<Map<String, Object>> getRanking(Integer limit);

    /**
     * 获取最新污染物数据列表
     */
    List<Map<String, Object>> getLatestList(Integer limit);
}
