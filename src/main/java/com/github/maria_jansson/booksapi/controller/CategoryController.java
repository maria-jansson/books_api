package com.github.maria_jansson.booksapi.controller;

import com.github.maria_jansson.booksapi.dto.CategoryDTO;
import com.github.maria_jansson.booksapi.service.CategoryService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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
    public ResponseEntity<CollectionModel<EntityModel<CategoryDTO>>> getAllCategories() {
        List<CategoryDTO> categories = categoryService.getAllCategories();
        List<EntityModel<CategoryDTO>> categoryModels = new ArrayList<>();
        for (CategoryDTO category : categories) {
            EntityModel<CategoryDTO> model = EntityModel.of(category);
            model.add(linkTo(methodOn(CategoryController.class).getOneCategory(category.id())).withSelfRel());
            categoryModels.add(model);
        }
        return ResponseEntity.ok(CollectionModel.of(categoryModels,
                linkTo(methodOn(CategoryController.class).getAllCategories()).withSelfRel()));
    }
}
