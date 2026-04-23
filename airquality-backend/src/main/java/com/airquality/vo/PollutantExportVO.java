package com.airquality.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.io.Serializable;

/**
 * 污染物导出VO
 */
@Data
public class PollutantExportVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 城市名
     */
    @ExcelProperty("城市")
    @ColumnWidth(15)
    private String city;

    /**
     * 污染物类型
     */
    @ExcelProperty("污染物类型")
    @ColumnWidth(15)
    private String pollutantType;

    /**
     * 浓度值
     */
    @ExcelProperty("浓度值")
    @ColumnWidth(12)
    private String concentration;

    /**
     * 单位
     */
    @ExcelProperty("单位")
    @ColumnWidth(12)
    private String unit;

    /**
     * 日期
     */
    @ExcelProperty("日期")
    @ColumnWidth(15)
    private String date;
}
