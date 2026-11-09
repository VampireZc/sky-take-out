package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface ReportMapper {
    // 查询营业额
    Double getTurnover(Map map);

    @Select("SELECT COUNT(*) FROM user WHERE create_time >= #{beginTime} AND create_time < #{endTime}")
    Long getNewUser(LocalDateTime beginTime, LocalDateTime endTime);

    @Select("SELECT COUNT(*) FROM user WHERE create_time < #{endTime}")
    Long getTotalUser(LocalDateTime endTime);

    @Select("SELECT COUNT(*) FROM orders WHERE status = 5 AND order_time between #{beginTime} AND #{endTime}")
    Long getValidOrderCount(LocalDateTime beginTime, LocalDateTime endTime);

    @Select("SELECT COUNT(*) FROM orders WHERE order_time < #{endTime}")
    Long getTotalOrderCount(LocalDateTime endTime);

    List<Map<String, Object>> getTopList(LocalDateTime beginTime, LocalDateTime endTime);
}