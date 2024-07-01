package com.quinbay.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO {
    private long cartId;
    private long userId;
    private List<ProductDTO> productsInCart;
    private int quantity;
    private double totalPrice;
    private String customerName;
    private String customerEmail;
}