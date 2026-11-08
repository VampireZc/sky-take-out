package com.sky.controller.user;

import com.sky.result.Result;
import com.sky.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/submit")
    public Result<String> submitOrder() {
        orderService.submitOrder();
        return Result.success("下单成功");
    }
}
