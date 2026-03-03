package com.github.maria_jansson.booksapi.controller;

import com.github.maria_jansson.booksapi.dto.BookDTO;
import com.github.maria_jansson.booksapi.dto.BookRequestDTO;
import com.github.maria_jansson.booksapi.service.BookService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * REST controller for managing books.
 * Provides endpoints for CRUD operations on book resources.
 */
@RestController
@RequestMapping("/api/v1/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<BookDTO>>> getAllBooks() {
        List<BookDTO> books = bookService.getAllBooks();
        List<EntityModel<BookDTO>> bookModels = new ArrayList<>();
        for (BookDTO book : books) {
            EntityModel<BookDTO> model = EntityModel.of(book);
            model.add(linkTo(methodOn(BookController.class).getOneBook(book.id())).withSelfRel());
            bookModels.add(model);
        }
        return ResponseEntity.ok(CollectionModel.of(bookModels,
                linkTo(methodOn(BookController.class).getAllBooks()).withSelfRel()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<BookDTO>> getOneBook(@PathVariable Long id) {
        BookDTO bookDTO = bookService.getOneBook(id);
        EntityModel<BookDTO> model = EntityModel.of(bookDTO);
        addLinksToModel(id, model);
        return ResponseEntity.ok(model);
    }

    @PostMapping
    public ResponseEntity<EntityModel<BookDTO>> createBook(@RequestBody BookRequestDTO data) {
        BookDTO createdBook = bookService.createBook(data);
        EntityModel<BookDTO> model = EntityModel.of(createdBook);
        Long id = createdBook.id();
        addLinksToModel(id, model);

        // Create and set resource location URL
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(location).body(model);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<BookDTO>> updateBook(@PathVariable Long id, @RequestBody BookRequestDTO data) {
        BookDTO updatedBook = bookService.updateBook(id, data);
        EntityModel<BookDTO> model = EntityModel.of(updatedBook);
        addLinksToModel(id, model);
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    private void addLinksToModel(Long id, EntityModel<BookDTO> model) {
        model.add(linkTo(methodOn(BookController.class).getOneBook(id)).withSelfRel());
        model.add(linkTo(methodOn(BookController.class).updateBook(id, null)).withRel("update"));
        model.add(linkTo(methodOn(BookController.class).deleteBook(id)).withRel("delete"));
    }
}