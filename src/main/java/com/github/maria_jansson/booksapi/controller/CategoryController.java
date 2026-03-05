package com.github.maria_jansson.booksapi.controller;

import com.github.maria_jansson.booksapi.dto.BookDTO;
import com.github.maria_jansson.booksapi.dto.CategoryDTO;
import com.github.maria_jansson.booksapi.dto.PageMetadata;
import com.github.maria_jansson.booksapi.dto.PagedResponse;
import com.github.maria_jansson.booksapi.service.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * REST controller for category resources.
 * Exposes read-only endpoints for retrieving categories.
 */
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController (CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<CategoryDTO>> getOneCategory(@PathVariable Long id) {
        CategoryDTO categoryDTO = categoryService.getOneCategory(id);
        EntityModel<CategoryDTO> model = EntityModel.of(categoryDTO);
        model.add(linkTo(methodOn(CategoryController.class).getOneCategory(id)).withSelfRel());

        return ResponseEntity.ok(model);
    }

    @GetMapping
    public ResponseEntity<PagedResponse<EntityModel<CategoryDTO>>> getAllCategories(@RequestParam Optional<String> categoryName, Pageable pageable) {
        Page<CategoryDTO> categories = categoryService.getAllCategories(categoryName, pageable);
        List<EntityModel<CategoryDTO>> categoryModels = new ArrayList<>();

        for (CategoryDTO category : categories) {
            EntityModel<CategoryDTO> model = EntityModel.of(category);
            model.add(linkTo(methodOn(CategoryController.class).getOneCategory(category.id())).withSelfRel());
            categoryModels.add(model);
        }

        CollectionModel<EntityModel<CategoryDTO>> collectionModel = CollectionModel.of(
                categoryModels, linkTo(methodOn(CategoryController.class).getAllCategories(categoryName, pageable)).withSelfRel());
        PageMetadata pageMetadata = new PageMetadata(
            categories.getNumber(),
            categories.getSize(),
            categories.getTotalElements(),
            categories.getTotalPages()
        );

        return ResponseEntity.ok(new PagedResponse<>(collectionModel, pageMetadata));
    }

    @GetMapping("/{id}/books")
    public ResponseEntity<PagedResponse<EntityModel<BookDTO>>> getAllBooksInCategory(@PathVariable Long id, Pageable pageable) {
        Page<BookDTO> books = categoryService.getBooksByCategory(id, pageable);
        List<EntityModel<BookDTO>> bookModels = new ArrayList<>();

        for (BookDTO book : books) {
            EntityModel<BookDTO> model = EntityModel.of(book);
            model.add(linkTo(methodOn(BookController.class).getOneBook(book.id())).withSelfRel());
            bookModels.add(model);
        }

        CollectionModel<EntityModel<BookDTO>> collectionModel = CollectionModel.of(
                bookModels,linkTo(methodOn(CategoryController.class).getAllBooksInCategory(id, pageable)).withSelfRel());
        PageMetadata pageMetadata = new PageMetadata(
            books.getNumber(),
            books.getSize(),
            books.getTotalElements(),
            books.getTotalPages()
        );

        return ResponseEntity.ok(new PagedResponse<>(collectionModel, pageMetadata));
    }
}
