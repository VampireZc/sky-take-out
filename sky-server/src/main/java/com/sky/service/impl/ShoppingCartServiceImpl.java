package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.DishDTO;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public void save(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        // 判断当前用户是否已经添加过该菜品
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);

        if (list != null && list.size() > 0){
            ShoppingCart shop = list.get(0);
            shop.setNumber(shop.getNumber() + 1);
            shoppingCartMapper.update(shop);
        }else {
            // 准备插入新数据
            Long dishId = shoppingCart.getDishId();
            Long setmealId = shoppingCart.getSetmealId();
            // 添加的是菜品
            if (dishId != null){
                DishDTO dish = dishMapper.getDish(dishId);
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setName(dish.getName());
                shoppingCart.setAmount(dish.getPrice());
            }else if (setmealId != null){
                // 添加的是套餐
                Setmeal setmeal = setmealMapper.getById(setmealId);
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setAmount(setmeal.getPrice());
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.save(shoppingCart);

        }


    }

    @Override
    public List<ShoppingCart> list() {
        return shoppingCartMapper.getAllList(BaseContext.getCurrentId());
    }

    @Override
    public void update(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if (list != null && list.size() > 0){
            ShoppingCart shop = list.get(0);
            Integer number = shop.getNumber();
            if (number > 1){
                shop.setNumber(number - 1);
                shoppingCartMapper.update(shop);
            }else {
                shoppingCartMapper.delete(shop.getId());
            }
        }
    }

    @Override
    public void clean() {
        shoppingCartMapper.clean(BaseContext.getCurrentId());
    }
}
