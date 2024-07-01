package com.quinbay.order.dto;

import com.quinbay.order.dao.Category;
import com.quinbay.order.dao.Seller;
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
    private int quantity;
    private Category category;
    private Seller seller;
}