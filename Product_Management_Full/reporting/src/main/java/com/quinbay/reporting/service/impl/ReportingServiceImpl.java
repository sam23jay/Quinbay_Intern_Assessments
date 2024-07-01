package com.quinbay.reporting.service.impl;

import com.quinbay.reporting.dto.OrderDTO;
import com.quinbay.reporting.dao.Order;
import com.quinbay.reporting.dao.OrderItem;
import com.quinbay.reporting.repository.OrderRepository;
import com.quinbay.reporting.service.ReportingService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportingServiceImpl implements ReportingService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    @KafkaListener(topics = "orders", groupId = "${kafka.groupId}")
    public void listen(OrderDTO orderDTO) {
        Order order = new Order();
        order.setOrderId(orderDTO.getOrderId());
        order.setTotalPrice(orderDTO.getTotalPrice());
        order.setQuantity(orderDTO.getQuantity());
        order.setUserId(orderDTO.getUserId());
        order.setCustomerName(orderDTO.getCustomerName());
        order.setCustomerEmail(orderDTO.getCustomerEmail());
        order.setOrderedOn(orderDTO.getOrderedOn());

        List<OrderItem> orderItems = orderDTO.getProducts().stream().map(productDTO -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(productDTO.getId());
            orderItem.setProductName(productDTO.getProductName());
            orderItem.setPrice(productDTO.getPrice());
            orderItem.setQuantity(productDTO.getQuantity());
            orderItem.setOrder(order);
            return orderItem;
        }).collect(Collectors.toList());

        order.setOrderItems(orderItems);

        orderRepository.save(order);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order getOrderById(long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
    }

    @Override
    public List<OrderItem> getOrderItemsByOrderId(long orderId) {
        Order order = getOrderById(orderId);
        return order.getOrderItems();
    }

    @Override
    public List<Order> getOrdersByProductName(String productName) {
        return orderRepository.findAll().stream()
                .filter(order -> order.getOrderItems().stream()
                        .anyMatch(item -> item.getProductName().equalsIgnoreCase(productName)))
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> getOrdersByCustomerName(String customerName) {
        return orderRepository.findAll().stream()
                .filter(order -> order.getCustomerName().equalsIgnoreCase(customerName))
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> getOrdersByCustomerEmail(String customerEmail) {
        return orderRepository.findAll().stream()
                .filter(order -> order.getCustomerEmail().equalsIgnoreCase(customerEmail))
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> getOrdersByDateRange(Date startDate, Date endDate) {
        return orderRepository.findAll().stream()
                .filter(order -> !order.getOrderedOn().before(startDate) && !order.getOrderedOn().after(endDate))
                .collect(Collectors.toList());
    }

    @Override
    public ByteArrayInputStream exportOrdersToExcel() {
        String[] columns = {"Order ID", "Total Price", "Quantity", "User ID", "Customer Name", "Customer Email", "Ordered On", "Product ID", "Product Name", "Product Price", "Product Quantity"};
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Orders");

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.BLACK.getIndex());

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerCellStyle);
            }

            List<Order> orders = getAllOrders();
            int rowId = 1;
            for (Order order : orders) {
                for (OrderItem item : order.getOrderItems()) {
                    Row row = sheet.createRow(rowId++);

                    row.createCell(0).setCellValue(order.getOrderId());
                    row.createCell(1).setCellValue(order.getTotalPrice());
                    row.createCell(2).setCellValue(order.getQuantity());
                    row.createCell(3).setCellValue(order.getUserId());
                    row.createCell(4).setCellValue(order.getCustomerName());
                    row.createCell(5).setCellValue(order.getCustomerEmail());
                    row.createCell(6).setCellValue(order.getOrderedOn().toString());
                    row.createCell(7).setCellValue(item.getProductId());
                    row.createCell(8).setCellValue(item.getProductName());
                    row.createCell(9).setCellValue(item.getPrice());
                    row.createCell(10).setCellValue(item.getQuantity());
                }
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel report", e);
        }
    }
}