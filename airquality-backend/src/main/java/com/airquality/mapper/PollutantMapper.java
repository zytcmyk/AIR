package com.airquality.mapper;

import com.airquality.dto.PollutantPieDTO;
import com.airquality.dto.PollutantSearchDTO;
import com.airquality.dto.PollutantTrendDTO;
import com.airquality.entity.PollutantData;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 污染物数据Mapper接口
 */
@Mapper
public interface PollutantMapper extends BaseMapper<PollutantData> {

    /**
     * 条件查询污染物数据
     */
    List<PollutantData> searchPollutant(@Param("dto") PollutantSearchDTO dto);

    /**
     * 获取某城市某污染物近N天趋势
     */
    List<PollutantTrendDTO> getTrendByCityAndPollutant(@Param("city") String city,
                                                        @Param("pollutantType") String pollutantType,
                                                        @Param("days") int days);

    /**
     * 获取某城市各污染物平均浓度占比
     */
    List<Map<String, Object>> getPollutantComposition(@Param("city") String city,
                                                       @Param("startDate") String startDate,
                                                       @Param("endDate") String endDate);

    /**
     * 获取某城市多个污染物指定日期的浓度对比
     */
    List<Map<String, Object>> getMultiPollutantCompare(@Param("city") String city,
                                                        @Param("pollutants") List<String> pollutants,
                                                        @Param("date") String date);

    /**
     * 获取最新污染物数据
     */
    List<PollutantData> getLatestPollutant(@Param("city") String city);

    /**
     * 统计城市数量
     */
    Integer countCities();

    /**
     * 统计记录数量
     */
    Integer countRecords();

    /**
     * 获取各污染物平均浓度
     */
    Map<String, Object> getAverageConcentrations(@Param("city") String city);

    /**
     * 获取达标率
     */
    Double getComplianceRate(@Param("city") String city);

    /**
     * 统计达标城市数量
     */
    Integer countCompliantCities();

    /**
     * 获取污染物浓度排名
     */
    List<Map<String, Object>> getRanking(@Param("limit") Integer limit);

    /**
     * 获取最新污染物数据列表
     */
    List<Map<String, Object>> getLatestList(@Param("limit") Integer limit);
}
