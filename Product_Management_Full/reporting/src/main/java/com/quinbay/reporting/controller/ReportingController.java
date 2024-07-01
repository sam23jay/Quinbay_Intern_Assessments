package com.quinbay.reporting.controller;

import com.quinbay.reporting.dao.Order;
import com.quinbay.reporting.dao.OrderItem;
import com.quinbay.reporting.service.ReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.io.InputStreamResource;
import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/reporting")
public class ReportingController {

    @Autowired
    private ReportingService reportingService;

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(reportingService.getAllOrders());
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable long orderId) {
        return ResponseEntity.ok(reportingService.getOrderById(orderId));
    }

    @GetMapping("/orders/{orderId}/items")
    public ResponseEntity<List<OrderItem>> getOrderItemsByOrderId(@PathVariable long orderId) {
        return ResponseEntity.ok(reportingService.getOrderItemsByOrderId(orderId));
    }

    @GetMapping("/orders/productName/{productName}")
    public ResponseEntity<List<Order>> getOrdersByProductName(@PathVariable String productName) {
        return ResponseEntity.ok(reportingService.getOrdersByProductName(productName));
    }

    @GetMapping("/orders/customerName/{customerName}")
    public ResponseEntity<List<Order>> getOrdersByCustomerName(@PathVariable String customerName) {
        return ResponseEntity.ok(reportingService.getOrdersByCustomerName(customerName));
    }

    @GetMapping("/orders/customerEmail/{customerEmail}")
    public ResponseEntity<List<Order>> getOrdersByCustomerEmail(@PathVariable String customerEmail) {
        return ResponseEntity.ok(reportingService.getOrdersByCustomerEmail(customerEmail));
    }

    @GetMapping("/orders/dateRange")
    public ResponseEntity<List<Order>> getOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {
        return ResponseEntity.ok(reportingService.getOrdersByDateRange(startDate, endDate));
    }

    @GetMapping("/orders/download")
    public ResponseEntity<InputStreamResource> downloadOrdersReport() {
        ByteArrayInputStream in = reportingService.exportOrdersToExcel();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=orders.xlsx");
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

        return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(in));
    }
}