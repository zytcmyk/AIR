package com.airquality.mapper;

import com.airquality.dto.*;
import com.airquality.entity.AqiData;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * AQI数据Mapper接口
 */
@Mapper
public interface AqiMapper extends BaseMapper<AqiData> {

    /**
     * 条件查询AQI数据
     */
    List<AqiData> searchAqi(@Param("dto") AqiSearchDTO dto);

    /**
     * 获取AQI排名（升序）
     */
    List<AqiRankingDTO> getAqiRanking(@Param("limit") int limit);

    /**
     * 获取某城市近N天AQI趋势
     */
    List<AqiTrendDTO> getTrendByCity(@Param("city") String city, @Param("days") int days);

    /**
     * 获取某城市某时间段内空气质量等级分布
     */
    List<AqiPieDTO> getQualityDistribution(@Param("city") String city,
                                            @Param("startDate") String startDate,
                                            @Param("endDate") String endDate);

    /**
     * 多城市对比（某日AQI）
     */
    List<AqiRankingDTO> getMultiCityCompare(@Param("cities") List<String> cities,
                                             @Param("date") String date);

    /**
     * 获取最新日期的AQI平均值
     */
    @Select("SELECT AVG(aqi_value) FROM aqi_data WHERE date = (SELECT MAX(date) FROM aqi_data)")
    Double getLatestAvgAqi();

    /**
     * 获取某城市最新日期的AQI值
     */
    @Select("SELECT aqi_value FROM aqi_data WHERE city = #{city} AND date = (SELECT MAX(date) FROM aqi_data) LIMIT 1")
    Integer getLatestAqiByCity(@Param("city") String city);

    /**
     * 获取最新日期
     */
    @Select("SELECT MAX(date) FROM aqi_data")
    String getLatestDate();

    /**
     * 获取昨日AQI平均值
     */
    @Select("SELECT AVG(aqi_value) FROM aqi_data WHERE date = DATE_SUB((SELECT MAX(date) FROM aqi_data), INTERVAL 1 DAY)")
    Double getYesterdayAvgAqi();

    /**
     * 获取某城市昨日的AQI值
     */
    @Select("SELECT aqi_value FROM aqi_data WHERE city = #{city} AND date = DATE_SUB((SELECT MAX(date) FROM aqi_data), INTERVAL 1 DAY) LIMIT 1")
    Integer getYesterdayAqiByCity(@Param("city") String city);

    /**
     * 获取最新日期各城市AQI数据
     */
    @Select("SELECT city, aqi_value AS aqiValue, quality_level AS qualityLevel, " +
            "DATE_FORMAT(date, '%Y-%m-%d') AS date FROM aqi_data " +
            "WHERE date = (SELECT MAX(date) FROM aqi_data) ORDER BY aqi_value ASC")
    List<AqiRankingDTO> getLatestCityAqi();

    /**
     * 获取城市数量
     */
    @Select("SELECT COUNT(DISTINCT city) FROM aqi_data")
    Integer getCityCount();

    /**
     * 获取记录总数
     */
    @Select("SELECT COUNT(*) FROM aqi_data")
    Integer getTotalCount();

    /**
     * 获取最新日期各城市AQI（带坐标）
     */
    List<CityAqiDTO> getLatestCityAqiWithCoords();

    /**
     * 获取所有城市列表（限制数量避免慢查询）
     */
    @Select("SELECT DISTINCT city FROM aqi_data ORDER BY city LIMIT 100")
    List<String> getAllCities();

    /**
     * 获取空气质量预警数据（AQI > 150）
     */
    @Select("SELECT city, aqi_value AS aqiValue, quality_level AS qualityLevel, " +
            "DATE_FORMAT(date, '%Y-%m-%d') AS date FROM aqi_data " +
            "WHERE aqi_value > 150 AND date = (SELECT MAX(date) FROM aqi_data) " +
            "ORDER BY aqi_value DESC")
    List<AqiRankingDTO> getAqiAlerts(@Param("city") String city);

    /**
     * 获取历史同比数据（按月统计）
     */
    @Select("SELECT DATE_FORMAT(date, '%Y-%m') AS date, " +
            "AVG(aqi_value) AS aqiValue " +
            "FROM aqi_data " +
            "WHERE YEAR(date) = #{year} " +
            "AND (#{city} IS NULL OR #{city} = '' OR city = #{city}) " +
            "GROUP BY DATE_FORMAT(date, '%Y-%m') " +
            "ORDER BY date")
    List<AqiTrendDTO> getYearCompare(@Param("city") String city, @Param("year") int year);
}
