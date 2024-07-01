package com.quinbay.inventory.controller;

import com.quinbay.inventory.dao.ProductHistory;
import com.quinbay.inventory.service.ProductHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/product-history")
public class ProductHistoryController {

    @Autowired
    private ProductHistoryService productHistoryService;

    @GetMapping("/getAll")
    public List<ProductHistory> getAllProductHistories() {
        return productHistoryService.getAllProductHistories();
    }

    @GetMapping("/get/{productId}")
    public ResponseEntity<List<ProductHistory>> getProductHistoryByProductId(@PathVariable long productId) {
        List<ProductHistory> productHistories = productHistoryService.getProductHistoryByProductId(productId);
        if (productHistories.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(productHistories, HttpStatus.OK);
    }
}