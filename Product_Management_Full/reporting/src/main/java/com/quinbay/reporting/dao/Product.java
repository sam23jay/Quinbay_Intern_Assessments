package com.quinbay.reporting.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class Product {
    private long id;
    private String productName;
    private double price;
    private int stock;
    private int quantity;


    public Product() {
        this.quantity = 0;
    }


}