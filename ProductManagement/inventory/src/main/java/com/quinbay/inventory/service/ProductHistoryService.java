package com.quinbay.inventory.service;

import com.quinbay.inventory.model.ProductHistory;
import com.quinbay.inventory.repository.ProductHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductHistoryService {

    @Autowired
    private ProductHistoryRepository productHistoryRepository;

    public List<ProductHistory> getAllProductHistories() {
        return productHistoryRepository.findAll();
    }

    public List<ProductHistory> getProductHistoryByProductId(long productId) {
        return productHistoryRepository.findByProductId(productId);
    }

    public void saveProductHistory(ProductHistory productHistory) {
        productHistoryRepository.save(productHistory);
    }
}