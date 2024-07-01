package com.quinbay.inventory.service;

import org.springframework.kafka.support.Acknowledgment;

public interface KafkaConsumerService {
    void consumeMessage(ProductMessage message, Acknowledgment acknowledgment);

    class ProductMessage {
        private long id;
        private int quantityRequested;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public int getQuantityRequested() {
            return quantityRequested;
        }

        public void setQuantityRequested(int quantityRequested) {
            this.quantityRequested = quantityRequested;
        }
    }
}
