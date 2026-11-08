package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        // 先拦截不正常的请求
        Long currentId = BaseContext.getCurrentId();

        List<ShoppingCart> spList = shoppingCartMapper.getAllList(currentId);

        // 购物车为空
        if (spList == null || spList.isEmpty()){
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        List<AddressBook> adList = addressMapper.list(currentId);

        // 地址为空
        if (adList == null || adList.isEmpty()){
            throw new ShoppingCartBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        // 新增订单
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.PENDING_PAYMENT);//待支付
        orders.setStatus(Orders.UN_PAID);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setPhone(adList.get(0).getPhone());
        orders.setConsignee(adList.get(0).getConsignee());
        orders.setUserId(currentId);
        orderMapper.submitOrder(orders);

        // 向订单明细表插入数据
        ArrayList<OrderDetail> list = new ArrayList<>();
        for (ShoppingCart sp : spList){
            OrderDetail detail = new OrderDetail();
            BeanUtils.copyProperties(sp, detail);
            detail.setOrderId(orders.getId());
            list.add(detail);
        }

        orderMapper.insertOrderDetail(list);

        // 情况购物车数据
        shoppingCartMapper.clean(currentId);

        // 构建返回前端的结构
        OrderSubmitVO build = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .build();

        return build;
    }
}
