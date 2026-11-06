package com.sky.controller.user;

import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/setmeal")
public class UserSetmealController {

    @Autowired
    private SetmealService setmealService;
    @GetMapping("/list")
    public Result<List<Setmeal>> getById(Long categoryId) {
        List<Setmeal> setmealVO = setmealService.list(categoryId);
        return Result.success(setmealVO);
    }

    @GetMapping("/dish/{id}")
    public Result<List<DishItemVO>> getSetmeal(@PathVariable Integer id) {
        List<DishItemVO> dishVO = setmealService.getSetmealDish(id);
        return Result.success(dishVO);
    }
}
