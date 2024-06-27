package com.quinbay.order.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data
@AllArgsConstructor
@Document(collection = "orders")
public class Order {
    @Id
    private long orderId;
    private double totalPrice;
    private Integer quantity;
    private List<Product> products;
    private long userId;
}
