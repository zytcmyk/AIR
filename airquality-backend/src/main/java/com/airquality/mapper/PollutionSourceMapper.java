package com.airquality.mapper;

import com.airquality.dto.CityRankingDTO;
import com.airquality.dto.SourceDistributionDTO;
import com.airquality.dto.SourceSearchDTO;
import com.airquality.dto.SourceTrendDTO;
import com.airquality.entity.PollutionSource;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 污染源数据Mapper接口
 */
@Mapper
public interface PollutionSourceMapper extends BaseMapper<PollutionSource> {

    /**
     * 条件查询污染源数据
     */
    List<PollutionSource> searchSource(@Param("dto") SourceSearchDTO dto);

    /**
     * 获取某城市各污染源平均贡献率
     */
    List<SourceDistributionDTO> getSourceDistributionByCity(@Param("city") String city,
                                                             @Param("startDate") String startDate,
                                                             @Param("endDate") String endDate);

    /**
     * 获取整体污染源类型分布（所有城市汇总）
     */
    List<SourceDistributionDTO> getOverallSourceDistribution(@Param("startDate") String startDate,
                                                              @Param("endDate") String endDate);

    /**
     * 获取某城市某污染源近N天贡献率趋势
     */
    List<SourceTrendDTO> getSourceTrendByCityAndType(@Param("city") String city,
                                                      @Param("sourceType") String sourceType,
                                                      @Param("days") int days);

    /**
     * 获取多城市某污染源贡献率对比
     */
    List<Map<String, Object>> getMultiCitySourceCompare(@Param("cities") List<String> cities,
                                                         @Param("sourceType") String sourceType,
                                                         @Param("date") String date);

    /**
     * 获取污染源排名
     */
    List<PollutionSource> getSourceRanking(@Param("limit") int limit, @Param("city") String city);

    /**
     * 获取按周期聚合的趋势数据
     * @param city 城市
     * @param period 周期类型: week/month/quarter
     * @param startDate 开始日期
     * @param endDate 结束日期
     */
    List<Map<String, Object>> getTrendByPeriod(@Param("city") String city,
                                                 @Param("period") String period,
                                                 @Param("startDate") String startDate,
                                                 @Param("endDate") String endDate);

    /**
     * 获取城市污染贡献排名
     * @param limit 限制数量
     * @param sourceType 污染源类型
     * @param startDate 开始日期
     * @param endDate 结束日期
     */
    List<CityRankingDTO> getCityRankingBySource(@Param("limit") int limit,
                                                  @Param("sourceType") String sourceType,
                                                  @Param("startDate") String startDate,
                                                  @Param("endDate") String endDate);

    /**
     * 获取多城市污染源结构对比数据
     * @param cities 城市列表
     * @param startDate 开始日期
     * @param endDate 结束日期
     */
    List<Map<String, Object>> getCitySourceStructureCompare(@Param("cities") List<String> cities,
                                                              @Param("startDate") String startDate,
                                                              @Param("endDate") String endDate);
}
