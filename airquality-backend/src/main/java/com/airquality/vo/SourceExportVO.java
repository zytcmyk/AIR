package com.airquality.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.io.Serializable;

/**
 * 污染源导出VO
 */
@Data
public class SourceExportVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 城市名
     */
    @ExcelProperty("城市")
    @ColumnWidth(15)
    private String city;

    /**
     * 污染源类型
     */
    @ExcelProperty("污染源类型")
    @ColumnWidth(15)
    private String sourceType;

    /**
     * 贡献率
     */
    @ExcelProperty("贡献率")
    @ColumnWidth(12)
    private String contributionRate;

    /**
     * 日期
     */
    @ExcelProperty("日期")
    @ColumnWidth(15)
    private String date;
}
