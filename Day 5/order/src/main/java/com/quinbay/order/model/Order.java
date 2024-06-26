package com.quinbay.order.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Document(collection = "orders")
public class Order {
    private long orderId;
    private double totalPrice;
    private Integer quantity;
    private List<Product> products;
    private long userId;
    private Timestamp orderDate;



    public Order() {
        this.products = new ArrayList<>();
    }
}
