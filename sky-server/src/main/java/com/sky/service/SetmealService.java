package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {

    // 新增
    void saveWithDish(SetmealDTO setmealDTO);

    // 分页查询
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    // 批量删除
    public void deleteBatch(List<Long> ids);

    // 根据id查询套餐
    SetmealVO getByIdWithDish(Long id);

    List<Setmeal> list(Long categoryId);

    // 修改
    void update(SetmealDTO setmealDTO);

    // 启用禁用
    void startOrStop(Integer status, Long id);

    List<DishItemVO> getSetmealDish(Integer id);
}
