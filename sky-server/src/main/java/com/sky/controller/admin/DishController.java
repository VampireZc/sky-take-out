package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @PostMapping
    public Result<String> addDish(@RequestBody DishDTO dishDto) {
        dishService.addDish(dishDto);
        return Result.success("新增菜品成功");
    }

    @GetMapping("/page")
    public Result<PageResult> pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    public Result<DishDTO> getDish(@PathVariable Long id) {
        DishDTO dishDto = dishService.getDish(id);
        return Result.success(dishDto);
    }

    @PutMapping
    public Result<String> updateDish(@RequestBody DishDTO dishDto) {
        dishService.updateDish(dishDto);
        return Result.success("修改菜品成功");
    }

    @DeleteMapping
    public Result<String> deleteDish(@RequestParam String ids) {
        dishService.deleteDishByIds(ids);
        return Result.success("删除菜品成功");
    }
}
