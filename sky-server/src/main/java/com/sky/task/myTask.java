package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class myTask {

    @Autowired
    private OrderMapper orderMapper;

    // 超时15分钟未支付取消订单
    // 每分钟执行一次
    @Scheduled(cron = "0 0 0 * * ?")
    public void cancelOrder() {
        // 查询待支付且超时的订单
        LocalDateTime time = LocalDateTime.now().minusMinutes(15);
        List<Orders> ordersList = orderMapper.PendingPay(Orders.PENDING_PAYMENT, time);
        if (ordersList != null && !ordersList.isEmpty()){
            ordersList.forEach(orders -> {
                log.info("订单超时未支付，系统自动取消，订单编号：{}", orders.getNumber());
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelTime(LocalDateTime.now());
                orders.setCancelReason("订单超时未支付，系统自动取消");
                orderMapper.update(orders);
            });
        }
    }

    // 每日零点自动完成订单
    @Scheduled(cron = "0 0 0 * * ?")
    public void completeOrder() {
        // 查询待完成订单
        List<Orders> ordersList = orderMapper.getPendingCompleteOrder();
        if (ordersList != null && !ordersList.isEmpty()) {
            ordersList.forEach(orders -> {
                if (orders.getStatus() == Orders.DELIVERY_IN_PROGRESS){
                    log.info("订单超时未完成，系统自动完成，订单编号：{}", orders.getNumber());
                    orders.setStatus(Orders.COMPLETED);
                    orders.setRemark("订单超时未完成，系统自动完成");
                    orderMapper.update(orders);
                }
            });
        }
    }

}
