package com.sky.mapper;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void update(ShoppingCart shop);

    void save(ShoppingCart shoppingCart);

    @Select("select * from shopping_cart where user_id = #{currentId}")
    List<ShoppingCart> getAllList(Long currentId);

    @Delete("delete from shopping_cart where id = #{id}")
    void delete(Long id);

    @Delete("delete from shopping_cart where user_id = #{currentId}")
    void clean(Long currentId);
}
