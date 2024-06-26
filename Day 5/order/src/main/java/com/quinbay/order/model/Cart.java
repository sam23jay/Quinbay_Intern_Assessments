package com.quinbay.order.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cart {
    private long cartId;
    private long userId;
    private List<Product> productsInCart = new ArrayList<>();
    private int quantity;
    private double totalPrice;
}
