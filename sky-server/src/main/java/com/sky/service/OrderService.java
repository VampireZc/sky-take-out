package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

import java.util.List;

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

    // 订单统计
    OrderStatisticsVO statisticsCount();

    // 自己组装数据
    OrderStatisticsVO getSelfCount();

    // 后台订单接单
    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    // 后台订单拒绝接单
    void rejection(OrdersRejectionDTO ordersRejectionDTO) throws Exception;

    // 后台订单取消
    void cancel(OrdersCancelDTO ordersCancelDTO) throws Exception;

    // 后台订单派送
    void delivery(Long id);

    // 后台订单完成
    void complete(Long id);
}
