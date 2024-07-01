package com.quinbay.inventory.service;

import com.quinbay.inventory.dto.ProductDTO;
import com.quinbay.inventory.dao.Category;
import com.quinbay.inventory.dao.Product;

import java.util.List;

public interface ProductService {
    ProductDTO getProductById(long id);
    List<ProductDTO> getAllProducts();
    Category cacheCategory(Category category);
    ProductDTO addProduct(ProductDTO productDTO);
    ProductDTO updateProduct(ProductDTO productDTO);
    void deleteProduct(long id);
    void validateProduct(ProductDTO productDTO);
    void logProductHistory(Product product, String modifiedColumn, String oldValue, String newValue);
}
