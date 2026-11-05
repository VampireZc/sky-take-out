package com.sky.mapper;

import com.sky.anno.AutoFill;
import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

import java.util.List;

@Mapper
public interface DishMapper {
    @AutoFill(value = OperationType.INSERT)
    public void insert(Dish dish);

    public void insertDishFlavor(List<DishFlavor> dishFlavor);

    public List<Dish> pageQuery(String name, Integer categoryId, Integer status);

    public DishDTO getDish(Long id);

    public List<DishFlavor> getDishFlavors(Long id);

    @AutoFill(value = OperationType.INSERT)
    public void updateDish(Dish dish);

    public void deleteDishByIds(Integer[] idArray);

    public void deleteFlavorById(Long id);
}
