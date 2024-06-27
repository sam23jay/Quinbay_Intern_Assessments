package com.quinbay.order.repository;

import com.quinbay.order.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface OrderRepository extends MongoRepository<Order, Long> {
    Order findByOrderId(long orderId);
    List<Order> findByUserId(long userId);
}
