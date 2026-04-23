package com.airquality.service;

import com.airquality.dto.CityRankingDTO;
import com.airquality.dto.SourceDistributionDTO;
import com.airquality.dto.SourceSearchDTO;
import com.airquality.dto.SourceTrendDTO;
import com.airquality.entity.PollutionSource;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 污染源服务接口
 */
public interface PollutionSourceService {

    /**
     * 条件查询污染源数据
     */
    List<PollutionSource> searchSource(SourceSearchDTO dto);

    /**
     * 获取某城市污染源占比（饼图数据）
     */
    Map<String, Object> getCitySourcePie(String city, String startDate, String endDate);

    /**
     * 获取整体污染源分布（饼图数据）
     */
    Map<String, Object> getOverallSourcePie(String startDate, String endDate);

    /**
     * 获取某城市某污染源趋势
     */
    List<SourceTrendDTO> getSourceTrend(String city, String sourceType, Integer days);

    /**
     * 获取多城市某污染源贡献率对比
     */
    List<Map<String, Object>> getMultiCitySourceCompare(List<String> cities, String sourceType, String date);

    /**
     * 导出污染源数据到Excel
     */
    void exportSource(SourceSearchDTO dto, HttpServletResponse response);

    /**
     * 获取污染源排名
     */
    List<PollutionSource> getSourceRanking(int limit, String city);

    /**
     * 获取按周期聚合的趋势数据
     * @param city 城市
     * @param period 周期类型: week/month/quarter
     * @param startDate 开始日期
     * @param endDate 结束日期
     */
    List<Map<String, Object>> getTrendByPeriod(String city, String period, String startDate, String endDate);

    /**
     * 获取城市污染贡献排名
     * @param limit 限制数量
     * @param sourceType 污染源类型
     * @param startDate 开始日期
     * @param endDate 结束日期
     */
    List<CityRankingDTO> getCityRankingBySource(int limit, String sourceType, String startDate, String endDate);

    /**
     * 获取多城市污染源结构对比数据
     * @param cities 城市列表
     * @param startDate 开始日期
     * @param endDate 结束日期
     */
    List<Map<String, Object>> getCitySourceStructureCompare(List<String> cities, String startDate, String endDate);
}
