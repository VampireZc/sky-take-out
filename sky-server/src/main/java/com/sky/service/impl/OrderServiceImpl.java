package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.mapper.UserMapper;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WeChatPayUtil weChatPayUtil;

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

    @Override
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        //调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }

    @Override
    public void paySuccess(String outTradeNo) {
        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }

    @Override
    public PageResult list(int pageNum, int pageSize, Integer status) {
        PageHelper.startPage(pageNum, pageSize);
        OrdersPageQueryDTO dto = new OrdersPageQueryDTO();
        dto.setStatus(status);
        dto.setUserId(BaseContext.getCurrentId());
        // 查询订单表
        Page<Orders> page = orderMapper.pageQuery(dto);
        ArrayList<OrderVO> list = new ArrayList<>();
        for (Orders orders : page) {
            Long orderId = orders.getId();
            // 查询订单详细表
            List<OrderDetail> orderDetailList = orderMapper.getOrderDetail(orderId);

            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(orders, orderVO);
            // 组装
            orderVO.setOrderDetailList(orderDetailList);
            list.add(orderVO);
        }

        return new PageResult(page.getTotal(), list);

    }

    @Override
    public OrderVO details(Long id) {
        Orders order = orderMapper.getById(id);
        List<OrderDetail> detail = orderMapper.getOrderDetail(id);
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);
        vo.setOrderDetailList(detail);
        return vo;
    }

    @Override
    public void userCancelById(Long id) throws Exception {
        Orders order = orderMapper.getById(id);
        if (order == null){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        //订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
        if (order.getStatus() > 2){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders = new Orders();
        orders.setId(order.getId());

        // 订单处于待接单状态取消需要先退款
        if (order.getStatus().equals(Orders.TO_BE_CONFIRMED)){
            weChatPayUtil.refund(
                    orders.getNumber(), //商户订单号
                    orders.getNumber(), //商户退款单号
                    new BigDecimal(0.01),//退款金额，单位 元
                    new BigDecimal(0.01));//原订单金额

            // 支付状态修改为 退款
            orders.setPayStatus(Orders.REFUND);
        }

        // 更新订单状态、取消原因、取消时间
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason("用户取消");
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    @Override
    public void repetition(Long id) {
        Long currentId = BaseContext.getCurrentId();
        List<OrderDetail> detail = orderMapper.getOrderDetail(id);
        List<ShoppingCart> cartList = detail.stream().map(item -> {
            ShoppingCart cart = new ShoppingCart();
            // 忽列id
            BeanUtils.copyProperties(item, cart, "id");
            cart.setUserId(currentId);
            cart.setCreateTime(LocalDateTime.now());
            return cart;
        }).collect(Collectors.toList());

        shoppingCartMapper.insertBatch(cartList);
    }
}
