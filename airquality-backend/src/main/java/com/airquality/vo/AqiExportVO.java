package com.airquality.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.io.Serializable;

/**
 * AQI导出VO
 */
@Data
public class AqiExportVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 城市名
     */
    @ExcelProperty("城市")
    @ColumnWidth(15)
    private String city;

    /**
     * AQI数值
     */
    @ExcelProperty("AQI值")
    @ColumnWidth(10)
    private Integer aqiValue;

    /**
     * 空气质量等级
     */
    @ExcelProperty("质量等级")
    @ColumnWidth(15)
    private String qualityLevel;

    /**
     * 日期
     */
    @ExcelProperty("日期")
    @ColumnWidth(15)
    private String date;
}
