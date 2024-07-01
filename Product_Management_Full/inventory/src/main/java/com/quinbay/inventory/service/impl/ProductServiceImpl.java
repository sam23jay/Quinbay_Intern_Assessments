package com.quinbay.inventory.service.impl;

import com.quinbay.inventory.dto.ProductDTO;
import com.quinbay.inventory.exception.CustomExceptions.*;
import com.quinbay.inventory.dao.Category;
import com.quinbay.inventory.dao.Product;
import com.quinbay.inventory.dao.ProductHistory;
import com.quinbay.inventory.dao.Seller;
import com.quinbay.inventory.repository.CategoryRepository;
import com.quinbay.inventory.repository.ProductRepository;
import com.quinbay.inventory.repository.SellerRepository;
import com.quinbay.inventory.service.ProductHistoryService;
import com.quinbay.inventory.service.ProductService;
import com.quinbay.inventory.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private ProductHistoryService productHistoryService;

    @Autowired
    private CategoryService categoryService;

    @Override
    public ProductDTO getProductById(long id) {
        logger.info("Fetching product by ID from the database: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        return convertToProductDTO(product);
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        logger.info("Fetching all products from the database");
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            throw new NoProductsAvailableException("No Products Available");
        }
        return products.stream().map(this::convertToProductDTO).collect(Collectors.toList());
    }

    @Override
    @CachePut(value = "categoryCache", key = "#category.id")
    public Category cacheCategory(Category category) {
        logger.info("Caching category with ID: {}", category.getId());
        return category;
    }

    @Override
    public ProductDTO addProduct(ProductDTO productDTO) {
        logger.info("Adding new product: {}", productDTO.getProductName());
        validateProduct(productDTO);
        Category category = categoryService.getCategoryById(productDTO.getCategory().getId());
        Seller seller = sellerRepository.findById(productDTO.getSeller().getId())
                .orElseThrow(() -> new SellerNotFoundException("Seller not found with id: " + productDTO.getSeller().getId()));
        Product product = new Product();
        product.setProductName(productDTO.getProductName());
        product.setPrice(productDTO.getPrice());
        product.setStock(productDTO.getStock());
        product.setCategory(category);
        product.setSeller(seller);
        Product savedProduct = productRepository.save(product);
        logger.info("Product added and saved to database with ID: {}", savedProduct.getId());

        cacheCategory(category);

        return convertToProductDTO(savedProduct);
    }

    @Override
    @CachePut(value = "productCache", key = "#productDTO.id")
    public ProductDTO updateProduct(ProductDTO productDTO) {
        logger.info("Updating product: {}", productDTO.getId());
        validateProduct(productDTO);
        Product existingProduct = productRepository.findById(productDTO.getId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productDTO.getId()));
        Category category = categoryService.getCategoryById(productDTO.getCategory().getId());
        Seller seller = sellerRepository.findById(productDTO.getSeller().getId())
                .orElseThrow(() -> new SellerNotFoundException("Seller not found with id: " + productDTO.getSeller().getId()));

        if (existingProduct.getSeller().getId() != productDTO.getSeller().getId()) {
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
        if (existingProduct.getCategory().getId() != productDTO.getCategory().getId()) {
            logProductHistory(existingProduct, "categoryId", String.valueOf(existingProduct.getCategory().getId()), String.valueOf(productDTO.getCategory().getId()));
        }

        existingProduct.setCategory(category);
        existingProduct.setSeller(seller);
        Product updatedProduct = productRepository.save(existingProduct);
        logger.info("Product updated and saved to database with ID: {}", updatedProduct.getId());

        cacheCategory(category);

        return convertToProductDTO(updatedProduct);
    }

    @Override
    @CacheEvict(value = "productCache", key = "#id")
    public void deleteProduct(long id) {
        logger.info("Deleting product: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        productRepository.delete(product);
        logger.info("Product deleted with ID: {}", id);
    }

    @Override
    public void validateProduct(ProductDTO productDTO) {
        if (productDTO.getProductName() == null || productDTO.getProductName().isEmpty()) {
            throw new InvalidProductDataException("Product name should not be empty");
        }
        if (productDTO.getStock() < 0 || productDTO.getPrice() <= 0) {
            throw new InvalidProductDataException("Invalid values for stock or price");
        }
    }

    @Override
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
        return new ProductDTO(product.getId(), product.getProductName(), product.getPrice(), product.getStock(), product.getCategory(), product.getSeller());
    }
}
