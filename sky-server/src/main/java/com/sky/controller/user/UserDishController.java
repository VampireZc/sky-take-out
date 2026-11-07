package com.sky.controller.user;

import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/dish")
@Slf4j
public class UserDishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/list")
    public Result<List<DishVO>> list(Long categoryId){
        String key = "dish_" + categoryId;
        ValueOperations ops = redisTemplate.opsForValue();
        if (redisTemplate.hasKey(key)) { // 从redis中获取数据
            log.info(" Dish from redis");
            List<DishVO> list = (List<DishVO>) ops.get(key);
            return Result.success(list);
        }else {
            List<DishVO> dish = dishService.getDishCategoryList(categoryId);
            ops.set(key, dish);
            return Result.success(dish);
        }
    }
}
