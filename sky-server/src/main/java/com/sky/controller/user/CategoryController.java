package com.sky.controller.user;

import com.sky.dto.CategoryDTO;
import com.sky.entity.Category;
import com.sky.result.Result;
import com.sky.service.SetCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/user/category")
public class CategoryController {

    @Autowired
    private SetCategoryService setCategoryService;

    @GetMapping("/list")
    public Result<List<Category>> list(Integer type){
        List<Category> category = setCategoryService.list(type);
        return Result.success(category);
    }
}
