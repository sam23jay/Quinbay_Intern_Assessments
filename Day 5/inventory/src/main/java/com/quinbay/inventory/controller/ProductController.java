package com.quinbay.inventory.controller;

import com.quinbay.inventory.model.Product;
import com.quinbay.inventory.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/get/{id}")
    public Product getProduct(@PathVariable("id") long productId) throws SQLException {
        return productService.getProductById(productId);
    }

    @GetMapping("/get")
    public List<Product> getAllProducts() throws SQLException {
        return productService.getAllProducts();
    }

    @PostMapping(value = "/post")
    @ResponseBody
    public String postProduct(@RequestBody Product product) throws SQLException {
        return productService.addProduct(product);
    }

    @PutMapping("/update")
    public Product updateProduct(@RequestBody Product updatedProduct) throws SQLException {
        return productService.updateProduct(updatedProduct);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") long productId) throws SQLException {
         return productService.deleteProduct(productId);
    }
}