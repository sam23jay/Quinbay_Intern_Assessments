package com.quinbay.order.service;

import com.quinbay.order.exception.CustomExceptions.CartNotFoundException;
import com.quinbay.order.exception.CustomExceptions.InsufficientStockException;
import com.quinbay.order.exception.CustomExceptions.ProductNotFoundException;
import com.quinbay.order.model.Cart;
import com.quinbay.order.model.Order;
import com.quinbay.order.model.Product;
import com.quinbay.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartService cartService;

    public Order placeOrder(long cartId, long userId) {
        Cart cart = cartService.getCart(cartId).getBody();
        if (cart == null || cart.getProductsInCart().isEmpty()) {
            throw new CartNotFoundException("Cart not found or is empty: " + cartId);
        }

        for (Product product : cart.getProductsInCart()) {
            if (!isStockAvailable(product)) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getProductName());
            }
        }

        long newOrderId = getNextOrderId();
        double totalPrice = cart.getProductsInCart().stream().mapToDouble(product -> product.getPrice() * product.getQuantity()).sum();
        Order order = new Order(newOrderId, totalPrice, cart.getQuantity(), cart.getProductsInCart(), userId);
        orderRepository.save(order);

        for (Product product : cart.getProductsInCart()) {
            updateProductStockInPostgres(product);
        }

        cartService.deleteCart(cartId);
        return order;
    }

    private long getNextOrderId() {
        return orderRepository.findAll().stream().mapToLong(Order::getOrderId).max().orElse(0) + 1;
    }

    private boolean isStockAvailable(Product product) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        Product inventoryProduct = restTemplate.exchange(
                "http://localhost:8881/product/get/" + product.getId(),
                HttpMethod.GET,
                entity,
                Product.class
        ).getBody();
        if (inventoryProduct != null) {
            return inventoryProduct.getStock() >= product.getQuantity();
        } else {
            throw new ProductNotFoundException("Product not found: " + product.getId());
        }
    }

    private void updateProductStockInPostgres(Product product) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        Product inventoryProduct = restTemplate.exchange(
                "http://localhost:8881/product/get/" + product.getId(),
                HttpMethod.GET,
                entity,
                Product.class
        ).getBody();
        if (inventoryProduct != null) {
            inventoryProduct.setStock(inventoryProduct.getStock() - product.getQuantity());
            HttpEntity<Product> updateEntity = new HttpEntity<>(inventoryProduct, headers);
            restTemplate.exchange(
                    "http://localhost:8881/product/update",
                    HttpMethod.PUT,
                    updateEntity,
                    Product.class
            );
        } else {
            throw new ProductNotFoundException("Product not found: " + product.getId());
        }
    }

    public List<Order> viewOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersByUserId(long userId) {
        return orderRepository.findByUserId(userId);
    }
}