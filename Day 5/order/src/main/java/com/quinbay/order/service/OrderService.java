package com.quinbay.order.service;

import com.quinbay.order.exception.CustomExceptions.CartNotFoundException;
import com.quinbay.order.exception.CustomExceptions.InsufficientStockException;
import com.quinbay.order.exception.CustomExceptions.ProductNotFoundException;
import com.quinbay.order.model.Cart;
import com.quinbay.order.model.Order;
import com.quinbay.order.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import jakarta.annotation.PostConstruct;
import org.bson.Document;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MongoDatabase mongoDatabase;

    @Autowired
    private CartService cartService;

    private MongoCollection<Document> orderCollection;
    private final Lock lock = new ReentrantLock();

    @PostConstruct
    public void init() {
        this.orderCollection = mongoDatabase.getCollection("orders");
    }

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
        Order order = new Order(newOrderId, totalPrice, cart.getQuantity(), cart.getProductsInCart(), userId, Timestamp.from(Instant.now()));
        orderCollection.insertOne(convertOrderToDocument(order));

        for (Product product : cart.getProductsInCart()) {
            updateProductStockInPostgres(product);
        }

        cartService.deleteCart(cartId);
        return order;
    }

    private long getNextOrderId() {
        Document latestOrder = orderCollection.find().sort(Sorts.descending("orderId")).first();
        if (latestOrder != null) {
            return latestOrder.getLong("orderId") + 1;
        } else {
            return 1L;
        }
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

    private Document convertOrderToDocument(Order order) {
        Document doc = new Document();
        doc.append("orderId", order.getOrderId());
        doc.append("totalPrice", order.getTotalPrice());
        doc.append("quantity", order.getQuantity());
        doc.append("products", order.getProducts().stream().map(this::convertProductToDocument).collect(Collectors.toList()));
        doc.append("userId", order.getUserId());
        doc.append("orderDate", order.getOrderDate());
        return doc;
    }

    private Document convertProductToDocument(Product product) {
        Document doc = new Document();
        doc.append("id", product.getId());
        doc.append("productName", product.getProductName());
        doc.append("price", product.getPrice());
        doc.append("stock", product.getStock());
        doc.append("quantity", product.getQuantity());
        return doc;
    }

    public List<Order> viewOrders() {
        List<Order> orders = new ArrayList<>();
        for (Document doc : orderCollection.find()) {
            orders.add(convertDocumentToOrder(doc));
        }
        return orders;
    }

    public List<Order> getOrdersByUserId(long userId) {
        List<Order> orders = new ArrayList<>();
        for (Document doc : orderCollection.find(new Document("userId", userId))) {
            orders.add(convertDocumentToOrder(doc));
        }
        return orders;
    }

    private Order convertDocumentToOrder(Document doc) {
        Order order = new Order();
        order.setOrderId(doc.getLong("orderId") != null ? doc.getLong("orderId") : 0L);
        order.setTotalPrice(doc.getDouble("totalPrice") != null ? doc.getDouble("totalPrice") : 0.0);
        order.setQuantity(doc.getInteger("quantity") != null ? doc.getInteger("quantity") : 0);
        order.setProducts(doc.getList("products", Document.class).stream().map(this::convertDocumentToProduct).collect(Collectors.toList()));
        order.setUserId(doc.getLong("userId") != null ? doc.getLong("userId") : 0L);
        Date orderDate = doc.getDate("orderDate");
        order.setOrderDate(orderDate != null ? new Timestamp(orderDate.getTime()) : new Timestamp(System.currentTimeMillis()));
        return order;
    }

    private Product convertDocumentToProduct(Document doc) {
        Product product = new Product();
        product.setId(doc.getLong("id") != null ? doc.getLong("id") : 0L);
        product.setProductName(doc.getString("productName") != null ? doc.getString("productName") : "");
        product.setPrice(doc.getDouble("price") != null ? doc.getDouble("price") : 0.0);
        product.setStock(doc.getInteger("stock") != null ? doc.getInteger("stock") : 0);
        product.setQuantity(doc.getInteger("quantity") != null ? doc.getInteger("quantity") : 0);
        return product;
    }
}
