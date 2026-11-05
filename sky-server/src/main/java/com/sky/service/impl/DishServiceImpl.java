package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Override
    @Transactional(rollbackFor = Exception.class) // 添加事务
    public void addDish(DishDTO dishDto) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDto, dish);
        dish.setStatus(StatusConstant.ENABLE);
        dishMapper.insert(dish);
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.forEach(flavor -> flavor.setDishId(dish.getId()));
        dishMapper.insertDishFlavor(flavors);
    }

    @Override
    public PageResult pageQuery(DishPageQueryDTO dis) {
        int page = dis.getPage();
        int pageSize = dis.getPageSize();
        String name = dis.getName();
        Integer categoryId = dis.getCategoryId();
        Integer status = dis.getStatus();

        PageHelper.startPage(page, pageSize);
        List<Dish> dishList = dishMapper.pageQuery(name, categoryId, status);
        Page<Dish> p = (Page<Dish>) dishList;;
        return new PageResult(p.getTotal(), p.getResult());
    }

    @Override
    public DishDTO getDish(Long id) {
        List<DishFlavor> flavors = dishMapper.getDishFlavors(id);// 获取菜品口味
        DishDTO dish = dishMapper.getDish(id);
        dish.setFlavors(flavors);
        return dish;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDish(DishDTO dishDto) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDto, dish);
        dishMapper.updateDish(dish);
        List<DishFlavor> flavors = dishDto.getFlavors();
        if (!flavors.isEmpty()){
            // 先删除菜品口味
            dishMapper.deleteFlavorById(dishDto.getId());
            // 再插入菜品口味
            dishMapper.insertDishFlavor(flavors);
        }

    }

    @Override
    public void deleteDishByIds(String ids) {
        Integer[] idArray = Arrays.stream(ids.split(","))
                .map(Integer::parseInt)
                .toArray(Integer[]::new);
        dishMapper.deleteDishByIds(idArray);
    }
}
