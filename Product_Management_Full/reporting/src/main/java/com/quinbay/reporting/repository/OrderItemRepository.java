package com.quinbay.reporting.repository;

import com.quinbay.reporting.dao.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}