package com.quinbay.order.dao;

import lombok.*;

@Data
@AllArgsConstructor
@ToString
public class Product {
    private long id;
    private String productName;
    private double price;
    private int stock;
    private int quantity;
    private Category category;
    private Seller seller;

    public Product() {
        this.quantity = 0;
    }


}