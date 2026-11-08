package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    @Operation(summary = "添加购物车")
    public Result<String> save(@RequestBody ShoppingCartDTO shoppingCartDTO){
        shoppingCartService.save(shoppingCartDTO);
        return Result.success("添加购物车成功");
    }

    @GetMapping("/list")
    @Operation(summary = "获取购物车列表")
    public Result<List<ShoppingCart>> list(){
        List<ShoppingCart> list = shoppingCartService.list();
        return Result.success(list);
    }

    @PostMapping("/sub")
    @Operation(summary = "删除购物车中商品")
    public Result<String> update(@RequestBody ShoppingCartDTO shoppingCartDTO){
        shoppingCartService.update(shoppingCartDTO);
        return Result.success("删除商品成功");
    }

    @DeleteMapping("/clean")
    @Operation(summary = "清空购物车")
    public Result<String> clean(){
        shoppingCartService.clean();
        return Result.success("清空购物车成功");
    }
}
