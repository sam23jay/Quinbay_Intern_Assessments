package com.quinbay.order.service.impl;

import com.quinbay.order.dto.CartDTO;
import com.quinbay.order.dto.OrderDTO;
import com.quinbay.order.dto.ProductDTO;
import com.quinbay.order.exception.CustomExceptions.CartNotFoundException;
import com.quinbay.order.exception.CustomExceptions.InsufficientStockException;
import com.quinbay.order.exception.CustomExceptions.ProductNotFoundException;
import com.quinbay.order.dao.Cart;
import com.quinbay.order.dao.Order;
import com.quinbay.order.dao.Product;
import com.quinbay.order.repository.OrderRepository;
import com.quinbay.order.service.CartService;
import com.quinbay.order.service.GmailService;
import com.quinbay.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import javax.mail.MessagingException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private KafkaTemplate<String, OrderDTO> orderDTOKafkaTemplate;

    @Autowired
    private GmailService gmailService;

    @Override
    public void sendMessage(OrderDTO orderDTO) {
        orderDTOKafkaTemplate.send("orders", orderDTO);
    }

    @Override
    public OrderDTO placeOrder(long cartId, long userId) {
        try {
            CartDTO cartDTO = cartService.getCart(cartId).getBody();
            if (cartDTO == null || cartDTO.getProductsInCart().isEmpty()) {
                throw new CartNotFoundException("Cart not found or is empty: " + cartId);
            }
            Cart cart = cartService.getCartFromDTO(cartDTO);

            for (Product product : cart.getProductsInCart()) {
                if (!isStockAvailable(product)) {
                    throw new InsufficientStockException("Insufficient stock for product: " + product.getProductName());
                }
            }

            long newOrderId = getNextOrderId();
            double totalPrice = cart.getProductsInCart().stream().mapToDouble(product -> product.getPrice() * product.getQuantity()).sum();
            int totalQuantity = cart.getProductsInCart().stream().mapToInt(Product::getQuantity).sum();
            Order order = new Order(newOrderId, totalPrice, totalQuantity, cart.getProductsInCart(), userId, cart.getCustomerName(), cart.getCustomerEmail(), new Date());
            orderRepository.save(order);
            OrderDTO orderDTO = convertToOrderDTO(order);
            for(Product product: cart.getProductsInCart()) {
                updateProductStockInPostgres(product);
            }
            sendMessage(orderDTO);
            sendOrderConfirmationEmail(orderDTO);
            cartService.deleteCart(cartId);
            return orderDTO;
        } catch (CartNotFoundException | InsufficientStockException | ProductNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("An unexpected error occurred while placing the order", ex);
        }
    }

    private long getNextOrderId() {
        return orderRepository.findAll().stream().mapToLong(Order::getOrderId).max().orElse(0) + 1;
    }

    private boolean isStockAvailable(Product product) {
        try {
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
        } catch (Exception ex) {
            throw new RuntimeException("Error checking stock availability", ex);
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
            if (inventoryProduct.getSeller() == null || inventoryProduct.getCategory() == null) {
                throw new ProductNotFoundException("Seller or Category information is missing for product: " + product.getId());
            }
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

    @Override
    public List<OrderDTO> viewOrders() {
        return orderRepository.findAll().stream().map(this::convertToOrderDTO).collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getOrdersByUserId(long userId) {
        return orderRepository.findByUserId(userId).stream().map(this::convertToOrderDTO).collect(Collectors.toList());
    }

    private OrderDTO convertToOrderDTO(Order order) {
        List<ProductDTO> productDTOs = order.getProducts().stream()
                .map(this::convertToProductDTO)
                .collect(Collectors.toList());

        return new OrderDTO(
                order.getOrderId(),
                order.getTotalPrice(),
                order.getQuantity(),
                productDTOs,
                order.getUserId(),
                order.getCustomerName(),
                order.getCustomerEmail(),
                order.getOrderedOn()
        );
    }

    private ProductDTO convertToProductDTO(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getProductName(),
                product.getPrice(),
                product.getStock(),
                product.getQuantity(),
                product.getCategory(),
                product.getSeller()
        );
    }

    private void sendOrderConfirmationEmail(OrderDTO orderDTO) {
        String to = orderDTO.getCustomerEmail();
        String from = "your-email@example.com";
        String subject = "Order Confirmation";
        String bodyText = "Dear " + orderDTO.getCustomerName() + ",\n\n" +
                "Thank you for your order. Your order ID is " + orderDTO.getOrderId() + ".\n" +
                "Order details:\n" +
                "Total Price: " + orderDTO.getTotalPrice() + "\n" +
                "Quantity: " + orderDTO.getQuantity() + "\n" +
                "Order Date: " + orderDTO.getOrderedOn() + "\n\n" +
                "Best regards,\n" +
                "Your Company";

        try {
            gmailService.sendMessage(to, from, subject, bodyText);
        } catch (MessagingException | IOException | GeneralSecurityException ex) {
        }
    }
}