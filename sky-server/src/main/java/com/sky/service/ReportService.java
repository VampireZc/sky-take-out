package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDate;

public interface ReportService {
    // 营业额统计
    TurnoverReportVO getTurnover(LocalDate begin, LocalDate end);

    // 用户统计
    UserReportVO getUserReport(LocalDate begin, LocalDate end);

    OrderReportVO getOrderReport(LocalDate begin, LocalDate end);

    SalesTop10ReportVO getTopList(LocalDate begin, LocalDate end);

    // 导出表格
    void exportExcel(HttpServletResponse response);

    /**
     * 使用 EasyExcel 导出运营数据报表（与 exportExcel 数据一致，实现方式不同）
     * @param response 用于向浏览器输出 xlsx 文件流
     */
    void exportExcelByEasyExcel(HttpServletResponse response);
}
