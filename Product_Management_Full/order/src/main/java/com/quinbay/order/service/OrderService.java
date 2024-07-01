package com.quinbay.order.service;

import com.quinbay.order.dto.OrderDTO;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

public interface OrderService {
    void sendMessage(OrderDTO orderDTO);

    OrderDTO placeOrder(long cartId, long userId);

    List<OrderDTO> viewOrders();

    List<OrderDTO> getOrdersByUserId(long userId);
}