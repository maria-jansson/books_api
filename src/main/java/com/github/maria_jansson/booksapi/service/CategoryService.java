package com.github.maria_jansson.booksapi.service;

import com.github.maria_jansson.booksapi.dto.BookDTO;
import com.github.maria_jansson.booksapi.dto.CategoryDTO;
import com.github.maria_jansson.booksapi.exception.ResourceNotFoundException;
import com.github.maria_jansson.booksapi.model.Book;
import com.github.maria_jansson.booksapi.model.Category;
import com.github.maria_jansson.booksapi.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepo;
    private final BookService bookService;

    public CategoryService (CategoryRepository categoryRepo, BookService bookService) {
        this.categoryRepo = categoryRepo;
        this.bookService = bookService;
    }

    public Page<CategoryDTO> getAllCategories(Optional<String> categoryName, Pageable pageable) {
        Page<Category> categories;

        if(categoryName.isPresent()) {
            categories = categoryRepo.findByNameContainingIgnoreCase(categoryName.get(), pageable);
        } else {
            categories = categoryRepo.findAll(pageable);
        }
        return categories.map(this::categoryToDTO);
    }

    public CategoryDTO getOneCategory(Long id) {
        Category category = categoryRepo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Category with id " + id + " not found."));
        return new CategoryDTO(category.getId(), category.getName());
    }

    public Page<BookDTO> getBooksByCategory(Long id, Pageable pageable) {
        return bookService.getBooksByCategory(id, pageable);
    }

    private CategoryDTO categoryToDTO(Category category) {
        return new CategoryDTO(
                category.getId(),
                category.getName()
        );
    }
}
