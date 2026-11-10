package com.sky.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 运营数据报表导出对象（EasyExcel 专用）
 * <p>
 * 与 BusinessDataVO 的区别：
 * 1. BusinessDataVO 服务于接口 JSON 返回，不关心表格长什么样；
 * 2. 本类专门服务于 Excel 导出，通过 @ExcelProperty 注解把「字段」和「表头名」绑定，
 *    EasyExcel 写表时会自动生成表头行和每一行的单元格，不需要像 POI 那样手工 getRow/getCell。
 * <p>
 * 注意：本类放在 sky-server 模块，是为了让 sky-pojo 模块不必依赖 easyexcel（保持 pojo 纯净）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessDataExcelVO implements Serializable {

    /**
     * @ExcelProperty(value = "表头名", index = 列下标)
     * value：写到 Excel 表头单元格里的文字，即列名
     * index：列顺序（从 0 开始），不写则按字段声明顺序排列
     */
    @ExcelProperty(value = "日期", index = 0)
    @ColumnWidth(24) // 汇总行内容较长（如 2026-10-12 至 2026-11-09），单独加大列宽
    private String date;

    @ExcelProperty(value = "营业额", index = 1)
    private Double turnover;

    @ExcelProperty(value = "有效订单数", index = 2)
    private Integer validOrderCount;

    @ExcelProperty(value = "订单完成率", index = 3)
    private Double orderCompletionRate;

    @ExcelProperty(value = "平均客单价", index = 4)
    private Double unitPrice;

    @ExcelProperty(value = "新增用户数", index = 5)
    private Integer newUsers;
}
