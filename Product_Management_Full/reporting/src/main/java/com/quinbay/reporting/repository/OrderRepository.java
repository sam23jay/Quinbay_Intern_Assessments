package com.quinbay.reporting.repository;

import com.quinbay.reporting.dao.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o JOIN o.orderItems oi WHERE oi.productName = :productName")
    List<Order> findOrdersByProductName(String productName);

    @Query("SELECT o FROM Order o WHERE o.customerName = :customerName")
    List<Order> findOrdersByCustomerName(String customerName);

    @Query("SELECT o FROM Order o WHERE o.customerEmail = :customerEmail")
    List<Order> findOrdersByCustomerEmail(String customerEmail);

    @Query("SELECT o FROM Order o WHERE o.orderedOn BETWEEN :startDate AND :endDate")
    List<Order> findOrdersByDateRange(Date startDate, Date endDate);
}