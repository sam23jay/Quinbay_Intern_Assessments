package com.quinbay.inventory.controller;

import com.quinbay.inventory.dto.CategoryDTO;
import com.quinbay.inventory.dto.CategoryProductsDTO;
import com.quinbay.inventory.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/getAll")
    public List<CategoryDTO> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/get/{id}")
    public CategoryDTO getCategoryById(@PathVariable long id) {
        return categoryService.getCategoryById(id);
    }

    @PostMapping("/add")
    public ResponseEntity<CategoryDTO> addCategory(@RequestBody CategoryDTO categoryDTO) {
        CategoryDTO newCategory = categoryService.addCategory(categoryDTO);
        return new ResponseEntity<>(newCategory, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/getProducts/{id}")
    public ResponseEntity<CategoryProductsDTO> getProductsByCategory(@PathVariable long id) {
        CategoryProductsDTO categoryProductsDTO = categoryService.getProductsByCategoryId(id);
        return new ResponseEntity<>(categoryProductsDTO, HttpStatus.OK);
    }
}