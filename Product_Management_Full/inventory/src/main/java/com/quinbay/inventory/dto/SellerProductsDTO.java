package com.quinbay.inventory.dto;

import com.quinbay.inventory.dao.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SellerProductsDTO {
    private long id;
    private String name;
    private String location;
    private List<Product> products;
}