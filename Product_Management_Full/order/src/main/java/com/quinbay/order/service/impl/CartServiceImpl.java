package com.quinbay.order.service.impl;

import com.quinbay.order.dto.AddToCartRequestDTO;
import com.quinbay.order.dto.CartDTO;
import com.quinbay.order.dto.ProductDTO;
import com.quinbay.order.dto.ProductQuantityDTO;
import com.quinbay.order.exception.CustomExceptions.InsufficientStockException;
import com.quinbay.order.exception.CustomExceptions.InvalidInputException;
import com.quinbay.order.exception.CustomExceptions.ProductNotFoundException;
import com.quinbay.order.dao.Cart;
import com.quinbay.order.dao.Product;
import com.quinbay.order.repository.CartRepository;
import com.quinbay.order.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CartRepository cartRepository;

    private List<Product> productList;

    private static final Pattern GMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@gmail\\.com$");

    @Override
    public void getProductDetails() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        this.productList = restTemplate.exchange(
                "http://localhost:8881/product/getAll",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<Product>>() {}
        ).getBody();
    }

    @Override
    public ResponseEntity<CartDTO> getCart(long cartId) {
        Optional<Cart> cartOpt = cartRepository.findById(cartId);
        if (cartOpt.isPresent()) {
            Cart cart = cartOpt.get();
            CartDTO cartDTO = mapToCartDTO(cart);
            return ResponseEntity.ok(cartDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @Override
    public Cart getCartFromDTO(CartDTO cartDTO) {
        if (cartDTO == null) {
            return null;
        }
        List<Product> productList = cartDTO.getProductsInCart().stream()
                .map(this::mapToProduct)
                .collect(Collectors.toList());
        return new Cart(cartDTO.getCartId(), cartDTO.getUserId(), productList, cartDTO.getQuantity(), cartDTO.getTotalPrice(), cartDTO.getCustomerName(), cartDTO.getCustomerEmail());
    }

    @Override
    public ResponseEntity<String> addToCart(long userId, AddToCartRequestDTO requestDTO) {
        try {
            long cartId = requestDTO.getCartId();
            String customerName = requestDTO.getCustomerName();
            String customerEmail = requestDTO.getCustomerEmail();
            if (customerName == null || customerName.trim().isEmpty()) {
                throw new InvalidInputException("Customer name cannot be empty");
            }
            if (customerEmail == null || customerEmail.trim().isEmpty() || !GMAIL_PATTERN.matcher(customerEmail).matches()) {
                throw new InvalidInputException("Invalid email address: " + customerEmail);
            }

            List<ProductQuantityDTO> products = requestDTO.getProducts();
            Cart cart = mapToCart(getCart(cartId).getBody());
            if (cart == null) {
                cart = createNewCart(cartId);
            }
            cart.setUserId(userId);
            cart.setCustomerName(customerName);
            cart.setCustomerEmail(customerEmail);
            getProductDetails();
            double totalPrice = 0.0;
            int totalQuantity = 0;
            for (ProductQuantityDTO productQuantity : products) {
                long productId = productQuantity.getId();
                int quantity = productQuantity.getQuantity();
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
                        cartProduct.setCategory(inventoryProduct.getCategory());
                        cartProduct.setSeller(inventoryProduct.getSeller());
                        cart.getProductsInCart().add(cartProduct);
                    } else {
                        cartProduct.setQuantity(cartProduct.getQuantity() + quantity);
                    }
                    totalPrice += inventoryProduct.getPrice() * quantity;
                    totalQuantity += quantity;
                    System.out.println(totalQuantity+" "+totalPrice);
                }
            }
            cart.setTotalPrice(totalPrice);
            cart.setQuantity(totalQuantity);
            System.out.println("Saving cart with totalQuantity: " + totalQuantity + ", totalPrice: " + totalPrice);
            cartRepository.save(cart);
            return ResponseEntity.status(HttpStatus.CREATED).body("Product added to cart");
        } catch (ProductNotFoundException | InsufficientStockException | InvalidInputException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @Override
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

    private CartDTO mapToCartDTO(Cart cart) {
        List<ProductDTO> productDTOList = cart.getProductsInCart().stream()
                .map(this::mapToProductDTO)
                .collect(Collectors.toList());
        return new CartDTO(cart.getCartId(), cart.getUserId(), productDTOList, cart.getQuantity(), cart.getTotalPrice(), cart.getCustomerName(), cart.getCustomerEmail());
    }

    private ProductDTO mapToProductDTO(Product product) {
        return new ProductDTO(product.getId(), product.getProductName(), product.getPrice(), product.getStock(), product.getQuantity(), product.getCategory(), product.getSeller());
    }

    private Cart mapToCart(CartDTO cartDTO) {
        if (cartDTO == null) {
            return null;
        }
        List<Product> productList = cartDTO.getProductsInCart().stream()
                .map(this::mapToProduct)
                .collect(Collectors.toList());
        return new Cart(cartDTO.getCartId(), cartDTO.getUserId(), productList, cartDTO.getQuantity(), cartDTO.getTotalPrice(), cartDTO.getCustomerName(), cartDTO.getCustomerEmail());
    }

    private Product mapToProduct(ProductDTO productDTO) {
        return new Product(productDTO.getId(), productDTO.getProductName(), productDTO.getPrice(), productDTO.getStock(), productDTO.getQuantity(), productDTO.getCategory(), productDTO.getSeller());
    }
}