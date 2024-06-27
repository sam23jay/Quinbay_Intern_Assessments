package com.quinbay.inventory.service;

import com.quinbay.inventory.dto.CategoryDTO;
import com.quinbay.inventory.dto.CategoryProductsDTO;
import com.quinbay.inventory.dto.ProductDTO;
import com.quinbay.inventory.exception.CustomExceptions.CategoryNotFoundException;
import com.quinbay.inventory.model.Category;
import com.quinbay.inventory.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductService productService;

    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream().map(this::convertToCategoryDTO).collect(Collectors.toList());
    }

    public CategoryDTO getCategoryById(long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
        return convertToCategoryDTO(category);
    }

    public CategoryDTO addCategory(CategoryDTO categoryDTO) {
        Category category = convertToCategory(categoryDTO);
        Category newCategory = categoryRepository.save(category);
        return convertToCategoryDTO(newCategory);
    }

    public void deleteCategory(long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
        categoryRepository.delete(category);
    }

    public CategoryProductsDTO getProductsByCategoryId(long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
        List<ProductDTO> productDTOs = category.getProducts().stream()
                .map(productService::convertToProductDTO)
                .collect(Collectors.toList());
        return new CategoryProductsDTO(category.getId(), category.getName(), productDTOs);
    }

    private CategoryDTO convertToCategoryDTO(Category category) {
        List<ProductDTO> productDTOs = category.getProducts() != null ?
                category.getProducts().stream().map(productService::convertToProductDTO).collect(Collectors.toList()) :
                List.of();
        return new CategoryDTO(category.getId(), category.getName(), productDTOs);
    }

    private Category convertToCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setName(categoryDTO.getName());
        return category;
    }
}