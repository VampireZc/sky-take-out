package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

public interface SetCategoryService {
    // 新增套餐分类
   public void addSetMeal(CategoryDTO categoryDTO);

   // 分页查询
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    // 删除分类
    public void delete(Long id);

    // 修改分类
    public void update(CategoryDTO categoryDTO);

    // 修改分类状态
    public void updateStatus(Long id, Integer status);
}
