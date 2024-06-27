package com.quinbay.inventory.service;

import com.quinbay.inventory.dto.ProductDTO;
import com.quinbay.inventory.exception.CustomExceptions.CategoryNotFoundException;
import com.quinbay.inventory.exception.CustomExceptions.InvalidProductDataException;
import com.quinbay.inventory.exception.CustomExceptions.NoProductsAvailableException;
import com.quinbay.inventory.exception.CustomExceptions.ProductNotFoundException;
import com.quinbay.inventory.exception.CustomExceptions.SellerNotFoundException;
import com.quinbay.inventory.exception.CustomExceptions.UnauthorizedSellerException;
import com.quinbay.inventory.model.Category;
import com.quinbay.inventory.model.Product;
import com.quinbay.inventory.model.ProductHistory;
import com.quinbay.inventory.model.Seller;
import com.quinbay.inventory.repository.CategoryRepository;
import com.quinbay.inventory.repository.ProductRepository;
import com.quinbay.inventory.repository.SellerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private ProductHistoryService productHistoryService;

    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            throw new NoProductsAvailableException("No Products Available");
        }
        return products.stream().map(this::convertToProductDTO).collect(Collectors.toList());
    }

    public ProductDTO getProductById(long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        return convertToProductDTO(product);
    }

    public ProductDTO addProduct(ProductDTO productDTO) {
        validateProduct(productDTO);
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + productDTO.getCategoryId()));
        Seller seller = sellerRepository.findById(productDTO.getSellerId())
                .orElseThrow(() -> new SellerNotFoundException("Seller not found with id: " + productDTO.getSellerId()));
        Product product = new Product();
        product.setProductName(productDTO.getProductName());
        product.setPrice(productDTO.getPrice());
        product.setStock(productDTO.getStock());
        product.setCategory(category);
        product.setSeller(seller);
        Product savedProduct = productRepository.save(product);
        return convertToProductDTO(savedProduct);
    }

    public ProductDTO updateProduct(ProductDTO productDTO) {
        validateProduct(productDTO);
        Product existingProduct = productRepository.findById(productDTO.getId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productDTO.getId()));
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + productDTO.getCategoryId()));
        Seller seller = sellerRepository.findById(productDTO.getSellerId())
                .orElseThrow(() -> new SellerNotFoundException("Seller not found with id: " + productDTO.getSellerId()));

        if (existingProduct.getSeller().getId() != productDTO.getSellerId()) {
            throw new UnauthorizedSellerException("Only the seller who added the product can update it");
        }

        if (!existingProduct.getProductName().equals(productDTO.getProductName())) {
            logProductHistory(existingProduct, "productName", existingProduct.getProductName(), productDTO.getProductName());
            existingProduct.setProductName(productDTO.getProductName());
        }
        if (existingProduct.getPrice() != productDTO.getPrice()) {
            logProductHistory(existingProduct, "price", String.valueOf(existingProduct.getPrice()), String.valueOf(productDTO.getPrice()));
            existingProduct.setPrice(productDTO.getPrice());
        }
        if (existingProduct.getStock() != productDTO.getStock()) {
            logProductHistory(existingProduct, "stock", String.valueOf(existingProduct.getStock()), String.valueOf(productDTO.getStock()));
            existingProduct.setStock(productDTO.getStock());
        }
        if (existingProduct.getCategory().getId() != productDTO.getCategoryId()) {
            logProductHistory(existingProduct, "categoryId", String.valueOf(existingProduct.getCategory().getId()), String.valueOf(productDTO.getCategoryId()));
        }

        existingProduct.setCategory(category);
        Product updatedProduct = productRepository.save(existingProduct);
        return convertToProductDTO(updatedProduct);
    }

    public void deleteProduct(long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        productRepository.delete(product);
    }

    public void validateProduct(ProductDTO productDTO) {
        if (productDTO.getProductName() == null || productDTO.getProductName().isEmpty()) {
            throw new InvalidProductDataException("Product name should not be empty");
        }
        if (productDTO.getStock() < 0 || productDTO.getPrice() <= 0) {
            throw new InvalidProductDataException("Invalid values for stock or price");
        }
    }

    public void logProductHistory(Product product, String modifiedColumn, String oldValue, String newValue) {
        ProductHistory productHistory = new ProductHistory();
        productHistory.setProductId(product.getId());
        productHistory.setModifiedColumn(modifiedColumn);
        productHistory.setOldValue(oldValue);
        productHistory.setNewValue(newValue);
        productHistory.setDateModified(convertToDate(LocalDateTime.now()));
        productHistoryService.saveProductHistory(productHistory);
    }

    public Date convertToDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public ProductDTO convertToProductDTO(Product product) {
        return new ProductDTO(product.getId(), product.getProductName(), product.getPrice(), product.getStock(), product.getCategory().getId(), product.getSeller().getId());
    }
}