package com.quinbay.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private long id;
    private String productName;
    private double price;
    private int stock;
    private long categoryId;
    private long sellerId;
}