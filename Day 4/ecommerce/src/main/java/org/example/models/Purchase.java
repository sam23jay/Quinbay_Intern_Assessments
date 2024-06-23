package org.example.models;

import lombok.Data;

import java.io.Serializable;
@Data
public class Purchase {
    private String productId;
    private int quantity;
    private double totalPrice;
    private String categoryName;

    public Purchase(String productId, int quantity, double totalPrice, String categoryName) {
        this.productId = productId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.categoryName = categoryName;
    }



    @Override
    public String toString() {
        return "Product ID: " + productId + ", Quantity: " + quantity + ", Total Price: " + totalPrice + ", Category: " + categoryName;
    }
}
