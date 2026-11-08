package com.sky.service;

import com.sky.dto.OrdersDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {
    // 提交订单
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    // 订单支付
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    // 支付成功执行
    void paySuccess(String outTradeNo);

    // 查询历史订单
    PageResult list(int pageNum, int pageSize, Integer status);

    // 查询订单详情
    OrderVO details(Long id);

    // 用户取消订单
    void userCancelById(Long id) throws Exception;

    // 再来一单
    void repetition(Long id);

    // 后台查询订单
    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);
}
