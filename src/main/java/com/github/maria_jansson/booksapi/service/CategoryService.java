package com.github.maria_jansson.booksapi.service;

import com.github.maria_jansson.booksapi.dto.CategoryDTO;
import com.github.maria_jansson.booksapi.exception.ResourceNotFoundException;
import com.github.maria_jansson.booksapi.model.Category;
import com.github.maria_jansson.booksapi.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepo;

    public CategoryService (CategoryRepository categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryRepo.findAll();
        List<CategoryDTO> categoryDTOs = new ArrayList<>();
        for (Category category : categories) {
            CategoryDTO categoryDto = new CategoryDTO(category.getId(), category.getName());
            categoryDTOs.add(categoryDto);
        }
        return categoryDTOs;
    }

    public CategoryDTO getOneCategory(Long id) {
        Category category = categoryRepo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Category with id " + id + " not found."));
        return new CategoryDTO(category.getId(), category.getName());
    }
}
