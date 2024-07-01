package com.quinbay.inventory.service.impl;

import com.quinbay.inventory.dto.CategoryDTO;
import com.quinbay.inventory.dto.CategoryProductsDTO;
import com.quinbay.inventory.dto.ProductDTO;
import com.quinbay.inventory.exception.CustomExceptions.CategoryNotFoundException;
import com.quinbay.inventory.dao.Category;
import com.quinbay.inventory.dao.Product;
import com.quinbay.inventory.repository.CategoryRepository;
import com.quinbay.inventory.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    @Cacheable(value = "categoryCache")
    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream().map(this::convertToCategoryDTO).collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "categoryCache", key = "#id")
    public Category getCategoryById(long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
    }

    @Override
    @CachePut(value = "categoryCache", key = "#result.id")
    public CategoryDTO addCategory(CategoryDTO categoryDTO) {
        Category category = convertToCategory(categoryDTO);
        Category newCategory = categoryRepository.save(category);
        return convertToCategoryDTO(newCategory);
    }

    @Override
    @CacheEvict(value = "categoryCache", key = "#id")
    public void deleteCategory(long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
        categoryRepository.delete(category);
    }

    @Override
    public CategoryProductsDTO getProductsByCategoryId(long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
        List<ProductDTO> productDTOs = category.getProducts().stream()
                .map(this::convertToProductDTO)
                .collect(Collectors.toList());
        return new CategoryProductsDTO(category.getId(), category.getName(), productDTOs);
    }

    @Override
    public CategoryDTO convertToCategoryDTO(Category category) {
        List<ProductDTO> productDTOs = category.getProducts() != null ?
                category.getProducts().stream().map(this::convertToProductDTO).collect(Collectors.toList()) :
                List.of();
        return new CategoryDTO(category.getId(), category.getName(), productDTOs);
    }

    @Override
    public Category convertToCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setName(categoryDTO.getName());
        return category;
    }

    public ProductDTO convertToProductDTO(Product product) {
        return new ProductDTO(product.getId(), product.getProductName(), product.getPrice(), product.getStock(), product.getCategory(), product.getSeller());
    }
}
