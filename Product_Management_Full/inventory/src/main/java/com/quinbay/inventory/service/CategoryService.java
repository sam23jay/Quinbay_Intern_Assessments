package com.quinbay.inventory.service;

import com.quinbay.inventory.dto.CategoryDTO;
import com.quinbay.inventory.dto.CategoryProductsDTO;
import com.quinbay.inventory.dao.Category;

import java.util.List;

public interface CategoryService {
    List<CategoryDTO> getAllCategories();
    Category getCategoryById(long id);
    CategoryDTO addCategory(CategoryDTO categoryDTO);
    void deleteCategory(long id);
    CategoryProductsDTO getProductsByCategoryId(long id);
    CategoryDTO convertToCategoryDTO(Category category);
    Category convertToCategory(CategoryDTO categoryDTO);
}
