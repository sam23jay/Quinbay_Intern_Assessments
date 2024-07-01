package com.quinbay.inventory.service.impl;

import com.quinbay.inventory.dao.ProductHistory;
import com.quinbay.inventory.repository.ProductHistoryRepository;
import com.quinbay.inventory.service.ProductHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductHistoryServiceImpl implements ProductHistoryService {

    @Autowired
    private ProductHistoryRepository productHistoryRepository;

    @Override
    public List<ProductHistory> getAllProductHistories() {
        return productHistoryRepository.findAll();
    }

    @Override
    public List<ProductHistory> getProductHistoryByProductId(long productId) {
        return productHistoryRepository.findByProductId(productId);
    }

    @Override
    public void saveProductHistory(ProductHistory productHistory) {
        productHistoryRepository.save(productHistory);
    }
}
