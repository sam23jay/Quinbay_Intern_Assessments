package com.quinbay.order.service;

import com.quinbay.order.dao.Cart;
import com.quinbay.order.dto.AddToCartRequestDTO;
import com.quinbay.order.dto.CartDTO;
import org.springframework.http.ResponseEntity;

public interface CartService {
    void getProductDetails();

    ResponseEntity<CartDTO> getCart(long cartId);

    Cart getCartFromDTO(CartDTO cartDTO);

    ResponseEntity<String> addToCart(long userId, AddToCartRequestDTO requestDTO);

    ResponseEntity<String> deleteCart(long cartId);
}