package com.quinbay.order.controller;

import com.quinbay.order.dto.OrderDTO;
import com.quinbay.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/place/{cartId}/{userId}")
    public OrderDTO placeOrder(@PathVariable long cartId, @PathVariable long userId) {
        return orderService.placeOrder(cartId, userId);
    }

    @GetMapping("/view")
    public List<OrderDTO> viewOrders() {
        return orderService.viewOrders();
    }

    @GetMapping("/user/{userId}")
    public List<OrderDTO> getOrdersByUserId(@PathVariable long userId) {
        return orderService.getOrdersByUserId(userId);
    }
}