package com.quinbay.inventory.service;

import com.quinbay.inventory.exception.CustomExceptions.InvalidProductDataException;
import com.quinbay.inventory.exception.CustomExceptions.NoProductsAvailableException;
import com.quinbay.inventory.exception.CustomExceptions.ProductNotFoundException;
import com.quinbay.inventory.model.Product;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private DataSource dataSource;

    private Connection connection;

    @PostConstruct
    private void initialize() throws SQLException {
        this.connection = dataSource.getConnection();
    }

    public List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM product";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                Product product = new Product(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getDouble("price"),
                        resultSet.getInt("stock")
                );
                products.add(product);
            }
        }
        if (products.isEmpty()) {
            throw new NoProductsAvailableException("No Products Available");
        }
        return products;
    }

    public Product getProductById(long id) throws SQLException {
        String sql = "SELECT * FROM product WHERE id = ?";
        Product product = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    product = new Product(
                            resultSet.getLong("id"),
                            resultSet.getString("name"),
                            resultSet.getDouble("price"),
                            resultSet.getInt("stock")
                    );
                }
            }
        }
        if (product == null) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
        return product;
    }

    public String addProduct(Product product) throws SQLException {
        if (product.getProductName() == null) {
            throw new InvalidProductDataException("Product name should not be empty");
        }
        if (product.getStock() < 0 || product.getPrice() <= 0) {
            throw new InvalidProductDataException("Invalid values for stock or price");
        }
        String sql = "INSERT INTO product (name, stock, price) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, product.getProductName());
            preparedStatement.setInt(2, product.getStock());
            preparedStatement.setDouble(3, product.getPrice());
            preparedStatement.executeUpdate();

        }
        return "Product added successfully";
    }

    public Product updateProduct(Product product) throws SQLException {
        if (product.getProductName() == null) {
            throw new InvalidProductDataException("Name cannot be null");
        }
        if (product.getStock() < 0 || product.getPrice() <= 0) {
            throw new InvalidProductDataException("Invalid values for stock or price");
        }
        String sql = "UPDATE product SET name = ?, stock = ?, price = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, product.getProductName());
            preparedStatement.setInt(2, product.getStock());
            preparedStatement.setDouble(3, product.getPrice());
            preparedStatement.setLong(4, product.getId());
            preparedStatement.executeUpdate();
        }
        return product;
    }

    public String deleteProduct(long id) throws SQLException {
        String sql = "DELETE FROM product WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                return "Product Deleted Successfully";
            }
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
    }
}
