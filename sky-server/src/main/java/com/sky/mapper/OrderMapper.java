package com.sky.mapper;

import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;

@Mapper
public interface OrderMapper {
    void submitOrder(Orders orders);

    void insertOrderDetail(ArrayList<OrderDetail> orderDetails);
}
