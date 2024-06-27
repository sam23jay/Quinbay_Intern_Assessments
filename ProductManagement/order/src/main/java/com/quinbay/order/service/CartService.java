package com.quinbay.order.service;

import com.quinbay.order.exception.CustomExceptions.InsufficientStockException;
import com.quinbay.order.exception.CustomExceptions.ProductNotFoundException;
import com.quinbay.order.model.Cart;
import com.quinbay.order.model.Product;
import com.quinbay.order.repository.CartRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CartRepository cartRepository;

    private List<Product> productList;

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
        Optional<Cart> cartOpt = cartRepository.findById(cartId);
        return cartOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    public ResponseEntity<String> addToCart(long userId, Map<String, Object> payload) {
        try {
            long cartId = Long.parseLong(payload.get("cartId").toString());
            List<Map<String, Object>> products = (List<Map<String, Object>>) payload.get("products");
            Cart cart = getCart(cartId).getBody();
            if (cart == null) {
                cart = createNewCart(cartId);
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
            cartRepository.save(cart);
            return ResponseEntity.status(HttpStatus.CREATED).body("Product added to cart");
        } catch (ProductNotFoundException | InsufficientStockException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    public ResponseEntity<String> deleteCart(long cartId) {
        try {
            cartRepository.deleteById(cartId);
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
        cartRepository.save(cart);
        return cart;
    }
}
