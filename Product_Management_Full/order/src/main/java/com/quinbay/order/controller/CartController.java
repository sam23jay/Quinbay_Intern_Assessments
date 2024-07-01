package com.quinbay.order.controller;

import com.quinbay.order.dto.AddToCartRequestDTO;
import com.quinbay.order.dto.CartDTO;
import com.quinbay.order.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/{cartId}")
    public ResponseEntity<CartDTO> getCart(@PathVariable long cartId) {
        return cartService.getCart(cartId);
    }

    @PostMapping("/add/{userId}")
    public ResponseEntity<String> addToCart(@PathVariable long userId, @RequestBody AddToCartRequestDTO requestDTO) {
        return cartService.addToCart(userId, requestDTO);
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<String> deleteCart(@PathVariable long cartId) {
        return cartService.deleteCart(cartId);
    }
}