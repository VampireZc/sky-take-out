package com.sky.controller.user;

import com.alibaba.fastjson.JSONObject;
import com.sky.dto.OrdersDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.webSocket.WebSocketServer;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/user/order")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/submit")
    @Operation(summary = "用户提交订单")
    public Result<OrderSubmitVO> submitOrder(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        OrderSubmitVO vo = orderService.submitOrder(ordersSubmitDTO);
        return Result.success(vo);
    }

    @PutMapping("/payment")
    @Operation(summary = "订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }

    @GetMapping("/historyOrders")
    @Operation(summary = "显示历史订单")
    public Result<PageResult> page(int page, int pageSize, Integer status){
        PageResult res = orderService.list(page, pageSize, status);
        return Result.success(res);
    }

    @GetMapping("/orderDetail/{id}")
    @Operation(summary = "查询订单详情")
    public Result<OrderVO> details(@PathVariable("id") Long id) {
        OrderVO orderVO = orderService.details(id);
        return Result.success(orderVO);
    }

    @PutMapping("/cancel/{id}")
    @Operation(summary = "取消订单")
    public Result cancel(@PathVariable("id") Long id) throws Exception {
        orderService.userCancelById(id);
        return Result.success();
    }

    @PostMapping("repetition/{id}")
    @Operation(summary = "再来一单")
    public Result<String> repetition(@PathVariable("id") Long id) {
        orderService.repetition(id);
        return Result.success();
    }

    // 客户催单
    @GetMapping("/reminder/{id}")
    @Operation(summary = "客户催单")
    public Result reminder(@PathVariable("id") Long id) {
        orderService.reminder(id);
        return Result.success("催单成功");
    }

}
