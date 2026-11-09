package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Mapper
public interface OrderMapper {
    void submitOrder(Orders orders);

    void insertOrderDetail(ArrayList<OrderDetail> orderDetails);

    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String outTradeNo);

    void update(Orders orders);

    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    @Select("select * from order_detail where order_id = #{orderId}")
    List<OrderDetail> getOrderDetail(Long orderId);

    @Select("select * from orders where id=#{id}")
    Orders getById(Long id);

    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer toBeConfirmed);

    List<Map<String, Object>> getSelfOrderCount();
}
