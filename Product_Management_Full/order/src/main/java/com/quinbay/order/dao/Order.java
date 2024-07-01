package com.quinbay.order.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "orders")
public class Order {
    @Id
    private long orderId;
    private double totalPrice;
    private Integer quantity;
    private List<Product> products;
    private long userId;
    private String customerName;
    private String customerEmail;
    private Date orderedOn;
}