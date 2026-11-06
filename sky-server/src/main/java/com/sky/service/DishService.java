package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    // 新增菜品
    public void addDish(DishDTO dishDto);

    // 分页查询
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    // 根据id查询菜品
    public DishDTO getDish(Long id);

    // 修改菜品
    public void updateDish(DishDTO dishDto);

    // 批量删除菜品
    public void deleteDishByIds(String ids);

    // 修改菜品状态
    public void updateDishStatus(Integer status, String id);

    // 根据分类id查询菜品列表
    public List<DishVO> getDishCategoryList(Long categoryId);
}
