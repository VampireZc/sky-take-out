package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/category")
public class SetCategoryController {
    @Autowired
    private SetCategoryService setCategoryService;

    @PostMapping
    @Operation(summary = "新增分类")
    public Result<String> addSetMeal(@RequestBody CategoryDTO categoryDTO) {
       setCategoryService.addSetMeal(categoryDTO);
       return Result.success("新增分类成功");
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    public Result<PageResult> list(CategoryPageQueryDTO categoryPageQueryDTO){
        PageResult category = setCategoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(category);
    }

    @DeleteMapping
    @Operation(summary = "删除分类")
    public Result<String> delete(@RequestParam Long id) {
        setCategoryService.delete(id);
        return Result.success("分类删除成功");
    }

    @PutMapping
    @Operation(summary = "修改分类")
    public Result<String> update(@RequestBody CategoryDTO categoryDTO) {
        setCategoryService.update(categoryDTO);
        return Result.success("修改分类成功");
    }

    @PostMapping("/status/{status}")
    @Operation(summary = "修改分类状态")
    public Result<String> updateStatus(@RequestParam Long id, @PathVariable Integer status) {
        setCategoryService.updateStatus(id, status);
        return Result.success("修改分类状态成功");
    }

}
