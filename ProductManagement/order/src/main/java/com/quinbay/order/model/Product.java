package com.quinbay.order.model;

import lombok.Data;

@Data
public class Product {
    private long id;
    private String productName;
    private double price;
    private int stock;
    private int quantity;

    public Product(long id, String productName, double price, int stock, int quantity) {
        this.id = id;
        this.productName = productName;
        this.price = price;
        this.stock = stock;
        this.quantity = quantity;
    }

    public Product() {
        this.quantity = 0;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", productName='" + productName + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                ", quantity=" + quantity +
                '}';
    }
}