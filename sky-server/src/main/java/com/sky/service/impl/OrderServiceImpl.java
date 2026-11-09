package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
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
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        AddressBook book = addressMapper.getById(ordersSubmitDTO.getAddressBookId());
        String address = book.getProvinceName() + book.getCityName() + book.getDistrictName() + book.getDetail();

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
        orders.setAddress(address);
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

    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        int page = ordersPageQueryDTO.getPage();
        int pageSize = ordersPageQueryDTO.getPageSize();
        PageHelper.startPage(page, pageSize);
        Page<Orders> p = orderMapper.pageQuery(ordersPageQueryDTO);
        // 需要返回订单菜品信息，自定义OrderVO响应结果
        List<OrderVO> orderVOS = getOrderVOList(p);
        return new PageResult(p.getTotal(), orderVOS);
    }

    @Override
    public OrderStatisticsVO statisticsCount() {// 根据状态，分别查询出待接单、待派送、派送中的订单数量
        Integer toBeConfirmed = orderMapper.countStatus(Orders.TO_BE_CONFIRMED);
        Integer confirmed = orderMapper.countStatus(Orders.CONFIRMED);
        Integer deliveryInProgress = orderMapper.countStatus(Orders.DELIVERY_IN_PROGRESS);

        // 将查询出的数据封装到orderStatisticsVO中响应
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setToBeConfirmed(toBeConfirmed);
        orderStatisticsVO.setConfirmed(confirmed);
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgress);
        return orderStatisticsVO;
    }

    private List<OrderVO> getOrderVOList(Page<Orders> page) {
        ArrayList<OrderVO> list = new ArrayList<>();
        List<Orders> ordersList = page.getResult();
        if (!CollectionUtils.isEmpty(ordersList)){ // 判断订单列表是否为空
            for (Orders orders : ordersList){
                OrderVO vo = new OrderVO();
                BeanUtils.copyProperties(orders, vo);
                // 组装菜品数量信息
                String orderDishes = getOrderDishesStr(orders);
                vo.setOrderDishes(orderDishes);
                list.add(vo);
            }
        }
        return list;
    }

    private String getOrderDishesStr(Orders orders) {
        List<OrderDetail> orderDetailList = orderMapper.getOrderDetail(orders.getId());
        // 将每一条订单菜品信息拼接为字符串（格式：宫保鸡丁*3；）
        List<String> orderDishList = orderDetailList.stream().map(x -> {
            String orderDish = x.getName() + "*" + x.getNumber() + ";";
            return orderDish;
        }).collect(Collectors.toList());
        // 将该订单对应的所有菜品信息拼接在一起
        return String.join("", orderDishList);
    }

    @Override
    public OrderStatisticsVO getSelfCount() {
        List<Map<String, Object>> selfOrderCount = orderMapper.getSelfOrderCount();
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        // 将map转换为 statusKey -> count的格式
        Map<String, Integer> map = selfOrderCount.stream()
                .collect(Collectors.toMap(
                        item ->String.valueOf(item.get("statusKey")),
                        item -> ((Number) item.get("count")).intValue())
                );
        orderStatisticsVO.setToBeConfirmed(map.getOrDefault("toBeConfirmed", 0));
        orderStatisticsVO.setConfirmed(map.getOrDefault("confirmed", 0));
        orderStatisticsVO.setDeliveryInProgress(map.getOrDefault("deliveryInProgress", 0));
        return orderStatisticsVO;
    }

    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersConfirmDTO, orders);
        orders.setStatus(Orders.CONFIRMED);
        orderMapper.update(orders);
    }

    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) throws Exception {
        Long id = ordersRejectionDTO.getId();
        Orders ordersDB = orderMapper.getById(id);
        // 订单只有存在且状态为2（待接单）才可以拒单
        if (ordersDB == null || !ordersDB.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Integer payStatus = ordersDB.getPayStatus();
        //用户已支付，需要退款
        if (payStatus.equals(Orders.PAID)){
            // 申请退款
            String refund = weChatPayUtil.refund(
                    ordersDB.getNumber(),
                    ordersDB.getNumber(),
                    new BigDecimal(0.01),
                    new BigDecimal(0.01));
            log.info("申请退款：{}", refund);
        }
        Orders orders = new Orders();
        orders.setId(ordersDB.getId());
        orders.setStatus(Orders.CANCELLED);
        orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    @Override
    public void cancel(OrdersCancelDTO ordersCancelDTO) throws Exception {
        // 根据id查询订单
        Orders ordersDB = orderMapper.getById(ordersCancelDTO.getId());

        //支付状态
        Integer payStatus = ordersDB.getPayStatus();
        if (payStatus == 1) {
            //用户已支付，需要退款
            String refund = weChatPayUtil.refund(
                    ordersDB.getNumber(),
                    ordersDB.getNumber(),
                    new BigDecimal(0.01),
                    new BigDecimal(0.01));
            log.info("申请退款：{}", refund);
        }

        // 管理端取消订单需要退款，根据订单id更新订单状态、取消原因、取消时间
        Orders orders = new Orders();
        orders.setId(ordersCancelDTO.getId());
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    @Override
    public void delivery(Long id) {
        Orders ordersDB = orderMapper.getById(id);
        // 校验订单是否存在，并且状态为3
        if (ordersDB == null || !ordersDB.getStatus().equals(Orders.CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders orders = new Orders();
        orders.setId(ordersDB.getId());
        // 更新订单状态,状态转为派送中
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);

        orderMapper.update(orders);
    }

    @Override
    public void complete(Long id) {
        // 根据id查询订单
        Orders ordersDB = orderMapper.getById(id);

        // 校验订单是否存在，并且状态为4
        if (ordersDB == null || !ordersDB.getStatus().equals(Orders.DELIVERY_IN_PROGRESS)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders = new Orders();
        orders.setId(ordersDB.getId());
        // 更新订单状态,状态转为完成
        orders.setStatus(Orders.COMPLETED);
        orders.setDeliveryTime(LocalDateTime.now());

        orderMapper.update(orders);
    }


}

