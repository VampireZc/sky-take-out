package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import java.time.LocalDate;

public interface ReportService {
    // 营业额统计
    TurnoverReportVO getTurnover(LocalDate begin, LocalDate end);

    // 用户统计
    UserReportVO getUserReport(LocalDate begin, LocalDate end);

    OrderReportVO getOrderReport(LocalDate begin, LocalDate end);

    SalesTop10ReportVO getTopList(LocalDate begin, LocalDate end);
}
