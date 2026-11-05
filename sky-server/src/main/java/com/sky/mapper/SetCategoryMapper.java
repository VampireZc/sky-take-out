package com.sky.mapper;

import com.sky.anno.AutoFill;
import com.sky.dto.CategoryDTO;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetCategoryMapper {

    @AutoFill(value = OperationType.UPDATE) // 切面编程
    public void save(Category category);

    List<Category> page(String name, Integer type);

    public void delete(Long id);

    @AutoFill(value = OperationType.UPDATE) // 切面编程
    public void update(Category category);
}
