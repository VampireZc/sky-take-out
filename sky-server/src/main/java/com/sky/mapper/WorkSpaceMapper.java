package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface WorkSpaceMapper {
    Integer orderCountByMap(Map map);

    Double sumByMap(Map map);

    Integer userCountByMap(Map map);

    Integer dishCountByMap(Map map);

    Integer srtmealCountByMap(Map map);
}
