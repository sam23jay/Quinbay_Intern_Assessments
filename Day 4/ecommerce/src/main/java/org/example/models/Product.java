package org.example.models;

import lombok.Data;

@Data

public class Product {
    private String productId;
    private String name;
    private double price;
    private int stock;
    private Category category;
    private boolean deleted;

    @Override
    public String toString() {
        return "Product ID: " + productId +
                "\nName: " + name +
                "\nPrice: " + price +
                "\nStock: " + stock +
                "\nCategory: " + (category != null ? category.getName() : "N/A") +
                "\nDeleted: " + deleted;
    }

   }

