package com.quinbay.inventory.service;

import com.quinbay.inventory.dao.ProductHistory;

import java.util.List;

public interface ProductHistoryService {
    List<ProductHistory> getAllProductHistories();
    List<ProductHistory> getProductHistoryByProductId(long productId);
    void saveProductHistory(ProductHistory productHistory);
}
