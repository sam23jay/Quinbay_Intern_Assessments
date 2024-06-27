package com.quinbay.order.controller;

import com.quinbay.order.model.Order;
import com.quinbay.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/place")
    public ResponseEntity<Order> placeOrder(@RequestParam long cartId, @RequestParam long userId) {
        Order order = orderService.placeOrder(cartId, userId);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/history")
    public ResponseEntity<List<Order>> viewOrders() {
        List<Order> orders = orderService.viewOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUserId(@PathVariable long userId) {
        List<Order> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }
}
