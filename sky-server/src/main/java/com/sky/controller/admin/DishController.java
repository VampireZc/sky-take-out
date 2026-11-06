package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @PostMapping
    @Operation(summary = "新增菜品")
    public Result<String> addDish(@RequestBody DishDTO dishDto) {
        dishService.addDish(dishDto);
        return Result.success("新增菜品成功");
    }

    @GetMapping("/page")
    @Operation(summary = "菜品分来查询")
    public Result<PageResult> pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据id查询菜品")
    public Result<DishDTO> getDish(@PathVariable Long id) {
        DishDTO dishDto = dishService.getDish(id);
        return Result.success(dishDto);
    }

    @PutMapping
    @Operation(summary = "修改菜品")
    public Result<String> updateDish(@RequestBody DishDTO dishDto) {
        dishService.updateDish(dishDto);
        return Result.success("修改菜品成功");
    }

    @DeleteMapping
    @Operation(summary = "删除菜品")
    public Result<String> deleteDish(@RequestParam String ids) {
        dishService.deleteDishByIds(ids);
        return Result.success("删除菜品成功");
    }

    @PostMapping("/status/{status}")
    @Operation(summary = "修改菜品状态")
    public Result<String> updateDishStatus(@PathVariable Integer status, @RequestParam String id) {
        dishService.updateDishStatus(status, id);
        return Result.success("修改菜品状态成功");
    }

    @GetMapping("/list")
    public Result<List<DishVO>> list(Long categoryId){
        List<DishVO> dish = dishService.getDishCategoryList(categoryId);
        return Result.success(dish);
    }
}
