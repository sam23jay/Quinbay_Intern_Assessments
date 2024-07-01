package com.quinbay.reporting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private long id;
    private long productId;
    private String productName;
    private double price;
    private int quantity;
}