package com.quinbay.inventory.dto;

import com.quinbay.inventory.dao.Category;
import com.quinbay.inventory.dao.Seller;
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
    private Category category;
    private Seller seller;
}
