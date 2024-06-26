package com.quinbay.inventory.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Product {
    private long id;
    private String productName;
    private double price;
    private int stock;
}