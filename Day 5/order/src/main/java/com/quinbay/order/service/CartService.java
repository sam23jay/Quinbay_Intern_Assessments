package com.quinbay.order.service;

import com.quinbay.order.exception.CustomExceptions.InsufficientStockException;
import com.quinbay.order.exception.CustomExceptions.ProductNotFoundException;
import com.quinbay.order.model.Cart;
import com.quinbay.order.model.Product;
import jakarta.annotation.PostConstruct;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MongoDatabase mongoDatabase;

    private MongoCollection<Document> cartCollection;
    private List<Product> productList;

    @PostConstruct
    public void init() {
        this.cartCollection = mongoDatabase.getCollection("carts");
    }

    public void getProductDetails() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        this.productList = restTemplate.exchange(
                "http://localhost:8881/product/get",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<Product>>() {}
        ).getBody();
    }

    public ResponseEntity<Cart> getCart(long cartId) {
        try {
            Document cartDoc = cartCollection.find(new Document("cartId", cartId)).first();
            if (cartDoc == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createNewCart(cartId));
            }
            return ResponseEntity.ok(convertDocumentToCart(cartDoc));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    public ResponseEntity<String> addToCart(long userId, Map<String, Object> payload) {
        try {
            long cartId = Long.parseLong(payload.get("cartId").toString());
            List<Map<String, Object>> products = (List<Map<String, Object>>) payload.get("products");
            Cart cart = getCart(cartId).getBody();
            if (cart == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cart not found");
            }
            cart.setUserId(userId);
            getProductDetails();
            double totalPrice = cart.getTotalPrice();
            int totalQuantity = cart.getQuantity();

            for (Map<String, Object> productMap : products) {
                long productId = Long.parseLong(productMap.get("id").toString());
                int quantity = Integer.parseInt(productMap.get("quantity").toString());
                Product inventoryProduct = productList.stream()
                        .filter(prod -> prod.getId() == productId)
                        .findFirst()
                        .orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));
                if (inventoryProduct.getStock() < quantity) {
                    throw new InsufficientStockException("Insufficient stock for product: " + inventoryProduct.getProductName());
                } else {
                    Product cartProduct = cart.getProductsInCart().stream()
                            .filter(prod -> prod.getId() == productId)
                            .findFirst()
                            .orElse(null);
                    if (cartProduct == null) {
                        cartProduct = new Product();
                        cartProduct.setId(inventoryProduct.getId());
                        cartProduct.setProductName(inventoryProduct.getProductName());
                        cartProduct.setPrice(inventoryProduct.getPrice());
                        cartProduct.setStock(inventoryProduct.getStock());
                        cartProduct.setQuantity(quantity);
                        cart.getProductsInCart().add(cartProduct);
                    } else {
                        cartProduct.setQuantity(cartProduct.getQuantity() + quantity);
                    }

                    totalPrice += inventoryProduct.getPrice() * quantity;
                    totalQuantity += quantity;
                }
            }
            cart.setTotalPrice(totalPrice);
            cart.setQuantity(totalQuantity);
            cartCollection.replaceOne(new Document("cartId", cartId), convertCartToDocument(cart));
            return ResponseEntity.status(HttpStatus.CREATED).body("Product added to cart");
        } catch (ProductNotFoundException | InsufficientStockException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }
    public ResponseEntity<String> deleteCart(long cartId) {
        try {
            cartCollection.deleteOne(new Document("cartId", cartId));
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Cart deleted");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    private Cart createNewCart(long cartId) {
        Cart cart = new Cart();
        cart.setCartId(cartId);
        cart.setProductsInCart(new ArrayList<>());
        cart.setQuantity(0);
        cart.setTotalPrice(0.0);
        Document cartDoc = convertCartToDocument(cart);
        cartCollection.insertOne(cartDoc);
        return cart;
    }

    private Document convertCartToDocument(Cart cart) {
        Document doc = new Document();
        doc.append("cartId", cart.getCartId());
        doc.append("userId", cart.getUserId());
        doc.append("productsInCart", cart.getProductsInCart().stream().map(this::convertProductToDocument).collect(Collectors.toList()));
        doc.append("quantity", cart.getQuantity());
        doc.append("totalPrice", cart.getTotalPrice());
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

    private Cart convertDocumentToCart(Document doc) {
        Cart cart = new Cart();
        cart.setCartId(doc.getLong("cartId"));
        cart.setUserId(doc.getLong("userId"));
        List<Document> productsDocList = (List<Document>) doc.get("productsInCart");
        List<Product> products = productsDocList.stream()
                .map(this::convertDocumentToProduct)
                .collect(Collectors.toList());
        cart.setProductsInCart(products);
        cart.setQuantity(doc.getInteger("quantity"));
        cart.setTotalPrice(doc.getDouble("totalPrice"));
        return cart;
    }

    private Product convertDocumentToProduct(Document doc) {
        Product product = new Product();
        product.setId(doc.getLong("id"));
        product.setProductName(doc.getString("productName"));
        product.setPrice(doc.getDouble("price"));
        product.setStock(doc.getInteger("stock"));
        product.setQuantity(doc.getInteger("quantity"));
        return product;
    }
}
