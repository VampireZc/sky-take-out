package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.ReportMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportMapper reportMapper;

    @Override
    public TurnoverReportVO getTurnover(LocalDate begin, LocalDate end) {
        ArrayList<LocalDate> timeList = generateDateList(begin, end);
        ArrayList<Double> numList = new ArrayList<>();
        for(LocalDate date : timeList){
            // 转成带时间的日期
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap<>();
            map.put("beginTime", beginTime);
            map.put("endTime", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = reportMapper.getTurnover(map);
            turnover = turnover == null ? 0.0 : turnover;
            numList.add(turnover);
        }
        String turnList = StringUtil.join(",", numList);
        String time = StringUtil.join(",", timeList);
        TurnoverReportVO turnoverReportVO = TurnoverReportVO.builder()
                .dateList(time)
                .turnoverList(turnList)
                .build();

        return turnoverReportVO;
    }

    @Override
    public UserReportVO getUserReport(LocalDate begin, LocalDate end) {
        ArrayList<LocalDate> timeList = generateDateList(begin, end);

        ArrayList<Long> newUserList = new ArrayList<>();
        ArrayList<Long> totalUserList = new ArrayList<>();

        for(LocalDate date : timeList){
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Long newUser = reportMapper.getNewUser(beginTime, endTime);
            Long totalUser = reportMapper.getTotalUser(endTime);
            newUserList.add(newUser);
            totalUserList.add(totalUser);
        }
        String newUser = StringUtil.join(",", newUserList);
        String totalUser = StringUtil.join(",", totalUserList);
        String time = StringUtil.join(",", timeList);
        return UserReportVO.builder()
                .dateList(time)
                .newUserList(newUser)
                .totalUserList(totalUser)
                .build();

    }

    @Override
    public OrderReportVO getOrderReport(LocalDate begin, LocalDate end) {
        ArrayList<LocalDate> timeList = generateDateList(begin, end);
        ArrayList<Long> isValidOrderCountList = new ArrayList<>();
        ArrayList<Long> totalOrderCountList = new ArrayList<>();

        for(LocalDate date : timeList){
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Long isValidOrderCount = reportMapper.getValidOrderCount(beginTime, endTime);
            Long totalOrderCount = reportMapper.getTotalOrderCount(endTime);
            isValidOrderCountList.add(isValidOrderCount);
            totalOrderCountList.add(totalOrderCount);
        }

        Integer isValidCount = isValidOrderCountList.stream().mapToInt(Long::intValue).sum();
        Integer total = totalOrderCountList.stream().mapToInt(Long::intValue).sum();
        Double orderCompletionRate = 0.0;
        if(total != 0){
            orderCompletionRate = isValidCount.doubleValue() / total;
        }
        String isValidOrderCount = StringUtil.join(",", isValidOrderCountList);
        String totalOrderCount = StringUtil.join(",", totalOrderCountList);
        String time = StringUtil.join(",", timeList);
        return OrderReportVO.builder()
                .dateList(time)
                .totalOrderCount(total)
                .validOrderCount(isValidCount)
                .orderCountList(totalOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .validOrderCountList(isValidOrderCount)
                .build();
    }

    @Override
    public SalesTop10ReportVO getTopList(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        List<Map<String, Object>> list =  reportMapper.getTopList(beginTime,endTime);

        String nameList = list.stream().map(s->String.valueOf(s.get("count")))
                .collect(Collectors.joining(","));

        String numberList = list.stream().map(s->String.valueOf(s.get("name")))
                .collect(Collectors.joining(","));

        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();

    }

    // 生成日期list
    private ArrayList<LocalDate> generateDateList(LocalDate begin, LocalDate end) {
        ArrayList<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        return dateList;
    }
}
