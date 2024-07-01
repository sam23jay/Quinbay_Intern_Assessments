package com.quinbay.reporting.service;

import com.quinbay.reporting.dto.OrderDTO;
import com.quinbay.reporting.dao.Order;
import com.quinbay.reporting.dao.OrderItem;
import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.List;

public interface ReportingService {
    void listen(OrderDTO orderDTO);
    List<Order> getAllOrders();
    Order getOrderById(long orderId);
    List<OrderItem> getOrderItemsByOrderId(long orderId);
    List<Order> getOrdersByProductName(String productName);
    List<Order> getOrdersByCustomerName(String customerName);
    List<Order> getOrdersByCustomerEmail(String customerEmail);
    List<Order> getOrdersByDateRange(Date startDate, Date endDate);
    ByteArrayInputStream exportOrdersToExcel();
}