package com.quinbay.reporting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private long orderId;
    private double totalPrice;
    private int quantity;
    private long userId;
    private String customerName;
    private String customerEmail;
    private Date orderedOn;
    private List<OrderItemDTO> products;
}