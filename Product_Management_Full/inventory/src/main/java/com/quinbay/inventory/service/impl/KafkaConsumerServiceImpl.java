package com.quinbay.inventory.service.impl;

import com.quinbay.inventory.dao.Product;
import com.quinbay.inventory.repository.ProductRepository;
import com.quinbay.inventory.service.KafkaConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerServiceImpl implements KafkaConsumerService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public void consumeMessage(ProductMessage message, Acknowledgment acknowledgment) {
        try {
            updateStock(message);
            acknowledgment.acknowledge();
        } catch (Exception ex) {
            // Log the exception
        }
    }

    private void updateStock(ProductMessage productMessage) {
        Product existingProduct = productRepository.findById(productMessage.getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        existingProduct.setStock(existingProduct.getStock() - productMessage.getQuantityRequested());
        productRepository.save(existingProduct);
    }
}
