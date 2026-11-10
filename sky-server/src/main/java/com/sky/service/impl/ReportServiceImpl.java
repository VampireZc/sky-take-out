package com.sky.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.sky.entity.Orders;
import com.sky.mapper.ReportMapper;
import com.sky.mapper.WorkSpaceMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.util.StringUtil;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportMapper reportMapper;

    @Autowired
    private WorkspaceService workspaceService;

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

    @Override
    public void exportExcel(HttpServletResponse response) {
        String url = "/Users/zc/my/IdeaProjects/waimai/sky-take-out/sky-server/src/main/resources/运营数据报表模板.xlsx";
        // 一个月
        LocalDate begin = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(1);
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        BusinessDataVO dataVO = workspaceService.getBusinessData(beginTime, endTime);
        // InputStream stream = this.getClass().getClassLoader().getResourceAsStream(url);

        try {
            InputStream stream = new FileInputStream(new File(url));
            // 开始获取表格模版对象
            XSSFWorkbook execl = new XSSFWorkbook(stream);

            // 获取sheet1
            XSSFSheet sheet1 = execl.getSheet("sheet1");
            // 填充数据
            sheet1.getRow(1).getCell(1).setCellValue(begin + "至" + end);

            // 填充第三行的数据
            XSSFRow row = sheet1.getRow(3);
            row.getCell(2).setCellValue(dataVO.getTurnover());
            row.getCell(4).setCellValue(dataVO.getOrderCompletionRate());
            row.getCell(6).setCellValue(dataVO.getNewUsers());

            // 填充第四行的数据
            row = sheet1.getRow(4);
            row.getCell(2).setCellValue(dataVO.getValidOrderCount());
            row.getCell(4).setCellValue(dataVO.getUnitPrice());

            // 填充日期具体数据
            for (int i = 0; i < 30; i++) {
                LocalDate date = begin.plusDays(i);
                LocalDateTime bgTime = LocalDateTime.of(date, LocalTime.MIN);
                LocalDateTime edTime = LocalDateTime.of(date, LocalTime.MAX);
                BusinessDataVO businessDataVO = workspaceService.getBusinessData(bgTime, edTime);

                XSSFRow row1 = sheet1.getRow(7 + i);
                row1.getCell(1).setCellValue(date.toString());
                row1.getCell(2).setCellValue(businessDataVO.getTurnover());
                row1.getCell(3).setCellValue(businessDataVO.getValidOrderCount());
                row1.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
                row1.getCell(5).setCellValue(businessDataVO.getUnitPrice());
                row1.getCell(6).setCellValue(businessDataVO.getNewUsers());

            }
            //通过输出流将文件下载到客户端浏览器中
            ServletOutputStream out = response.getOutputStream();
            execl.write(out);
            //关闭资源
            out.flush();
            out.close();
            execl.close();

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }


    }

    /**
     * 使用 EasyExcel 导出运营数据报表
     * <p>
     * 与 POI 版本（exportExcel）的实现对比：
     * POI：加载 xlsx 模板 -> getSheet/getRow/getCell -> 逐格 setCellValue -> workbook.write(输出流)
     * EasyExcel：查询数据封装成 List<VO> -> 一行链式调用自动生成表头和数据行 -> 写入 response 输出流
     * <p>
     * EasyExcel 的核心思想是「注解 + 反射」：VO 字段上的 @ExcelProperty 决定列名和列顺序，
     * 写入时由框架遍历 List，把每个对象的字段值依次填到对应单元格，无需手工操作 Row/Cell。
     */
    @Override
    public void exportExcelByEasyExcel(HttpServletResponse response) {
        // ---------- 1. 准备数据：统计区间与 POI 版本保持一致，最近 30 天 ----------
        LocalDate begin = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(1);

        // Excel 最终写入的就是这个 List：第 1 个元素是汇总行，后面 30 个元素是每日明细
        List<BusinessDataExcelVO> excelList = new ArrayList<>();

        // 1.1 汇总行：整段区间的经营数据（对应 POI 模板中第 3、4 行的概览区域）
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        BusinessDataVO summary = workspaceService.getBusinessData(beginTime, endTime);
        excelList.add(BusinessDataExcelVO.builder()
                .date(begin + " 至 " + end)
                .turnover(summary.getTurnover())
                .validOrderCount(summary.getValidOrderCount())
                .orderCompletionRate(summary.getOrderCompletionRate())
                .unitPrice(summary.getUnitPrice())
                .newUsers(summary.getNewUsers())
                .build());

        // 1.2 明细行：每天一条记录（对应 POI 模板中第 7 行开始的日期数据区域）
        for (int i = 0; i < 30; i++) {
            LocalDate date = begin.plusDays(i);
            LocalDateTime dayBegin = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX);
            BusinessDataVO dayData = workspaceService.getBusinessData(dayBegin, dayEnd);
            excelList.add(BusinessDataExcelVO.builder()
                    .date(date.toString())
                    .turnover(dayData.getTurnover())
                    .validOrderCount(dayData.getValidOrderCount())
                    .orderCompletionRate(dayData.getOrderCompletionRate())
                    .unitPrice(dayData.getUnitPrice())
                    .newUsers(dayData.getNewUsers())
                    .build());
        }

        try {
            // ---------- 2. 设置响应头：告诉浏览器“这是要下载的 xlsx 文件” ----------
            // xlsx 的标准 MIME 类型，浏览器收到后会弹出下载框而不是直接展示文件内容
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            // 文件名含中文必须做 URL 编码，否则下载后的文件名会乱码；
            // filename*=utf-8''xxx 是 RFC 5987 规定的写法，主流浏览器都兼容
            String fileName = URLEncoder.encode("运营数据报表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            // ---------- 3. 写表：EasyExcel 的核心链式调用 ----------
            EasyExcel.write(response.getOutputStream(), BusinessDataExcelVO.class)
                    // 注册写处理器：按单元格内容自动调整列宽（不注册则只用注解里设置的固定列宽）
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    // 指定工作表名称，相当于 POI 中拿到/创建 Sheet 对象
                    .sheet("运营数据报表")
                    // 把 List 写出去：表头来自 VO 的 @ExcelProperty，
                    // EasyExcel 内部会自动创建行和单元格并关闭输出流，无需手动 flush/close
                    .doWrite(excelList);
        } catch (IOException e) {
            // 输出流异常（如客户端提前断开连接）统一转为运行时异常，交给全局异常处理器
            throw new RuntimeException("EasyExcel 导出运营数据报表失败", e);
        }
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
