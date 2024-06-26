package com.quinbay.order.controller;

import com.quinbay.order.model.Cart;
import com.quinbay.order.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/get")
    public ResponseEntity<Cart> getCart(@RequestParam long cartId) {
        return cartService.getCart(cartId);
    }

    @PostMapping("/post")
    public ResponseEntity<String> addToCart(@RequestParam long userId, @RequestBody Map<String, Object> payload) {
        return cartService.addToCart(userId, payload);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteCart(@RequestParam long cartId) {
        return cartService.deleteCart(cartId);
    }
}
