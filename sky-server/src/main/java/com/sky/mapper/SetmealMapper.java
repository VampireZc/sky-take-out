package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.anno.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {

    @AutoFill(OperationType.INSERT)
    void insert(Setmeal setmeal);

    @AutoFill(OperationType.INSERT)
    public void insertBatch(List<SetmealDish> setmealDishes);

    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    @Select("select * from setmeal where id = #{id}")
    Setmeal getById(Long id);

    @Select("select * from setmeal_dish  where setmeal_id = #{id}")
    List<SetmealDish> getBySetmealId(Long id);


    @Delete("delete from setmeal where id = #{id}")
    void deleteById(Long setmealId);

    @Delete("delete from setmeal_dish where setmeal_id = #{id}")
    void deleteDishBySetmealId(Long id);


    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);

    List<Setmeal> list(Setmeal setmeal);

    @Select("select * from setmeal_dish where setmeal_id = #{id}")
    List<DishItemVO> getSetmealDish(Integer id);
}
