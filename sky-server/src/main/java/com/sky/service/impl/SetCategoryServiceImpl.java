package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.mapper.SetCategoryMapper;
import com.sky.result.PageResult;
import com.sky.service.SetCategoryService;
import com.sky.vo.OptionVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.beancontext.BeanContext;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SetCategoryServiceImpl implements SetCategoryService {
    @Autowired
    private SetCategoryMapper setCategoryMapper;

    @Override
    public void addSetMeal(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);

//        category.setCreateTime(LocalDateTime.now());
//        category.setUpdateTime(LocalDateTime.now());
//        category.setUpdateUser(BaseContext.getCurrentId());
//        category.setCreateUser(BaseContext.getCurrentId());

        category.setStatus(StatusConstant.ENABLE);

        setCategoryMapper.save(category);
    }

    @Override
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        String name = categoryPageQueryDTO.getName();
        Integer type = categoryPageQueryDTO.getType();
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        List<Category> categoryList = setCategoryMapper.page(name, type);
        Page page = (Page) categoryList;
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void delete(Long id) {
        setCategoryMapper.delete(id);
    }

    @Override
    public void update(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);

//        category.setUpdateTime(LocalDateTime.now());
//        category.setUpdateUser(BaseContext.getCurrentId());

        setCategoryMapper.update(category);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Category category = Category.builder().id(id).status(status).build();
        setCategoryMapper.update(category);
    }

    @Override
    public List<Category> list(Integer type) {
        return setCategoryMapper.list(type);
    }
}
