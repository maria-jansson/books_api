package com.github.maria_jansson.booksapi.controller;

import com.github.maria_jansson.booksapi.dto.AuthorDTO;

import com.github.maria_jansson.booksapi.dto.BookDTO;
import com.github.maria_jansson.booksapi.dto.PageMetadata;
import com.github.maria_jansson.booksapi.dto.PagedResponse;
import com.github.maria_jansson.booksapi.service.AuthorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * REST controller for author resources.
 * Exposes read-only endpoints for retrieving authors.
 */
@RestController
@RequestMapping("/api/v1/authors")
public class AuthorController {
    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<AuthorDTO>> getOneAuthor(@PathVariable Long id) {
        AuthorDTO authorDTO = authorService.getOneAuthor(id);
        EntityModel<AuthorDTO> model = EntityModel.of(authorDTO);
        model.add(linkTo(methodOn(AuthorController.class).getOneAuthor(id)).withSelfRel());

        return ResponseEntity.ok(model);
    }

    @GetMapping
    public ResponseEntity<PagedResponse<EntityModel<AuthorDTO>>> getAllAuthors(@RequestParam Optional<String> authorName, Pageable pageable) {
        Page<AuthorDTO> authors = authorService.getAllAuthors(authorName, pageable);
        List<EntityModel<AuthorDTO>> authorModels = new ArrayList<>();

        for (AuthorDTO author : authors) {
            EntityModel<AuthorDTO> model = EntityModel.of(author);
            model.add(linkTo(methodOn(AuthorController.class).getOneAuthor(author.id())).withSelfRel());
            authorModels.add(model);
        }

        CollectionModel<EntityModel<AuthorDTO>> collectionModel = CollectionModel.of(
                authorModels,linkTo(methodOn(AuthorController.class).getAllAuthors(authorName, pageable)).withSelfRel());
        PageMetadata pageMetadata = new PageMetadata(
            authors.getNumber(),
            authors.getSize(),
            authors.getTotalElements(),
            authors.getTotalPages()
        );

        return ResponseEntity.ok(new PagedResponse<>(collectionModel, pageMetadata));
    }

    @GetMapping("/{id}/books")
    public ResponseEntity<PagedResponse<EntityModel<BookDTO>>> getAllBooksByAuthor(@PathVariable Long id, Pageable pageable) {
        Page<BookDTO> books = authorService.getBooksByAuthor(id, pageable);
        List<EntityModel<BookDTO>> bookModels = new ArrayList<>();

        for (BookDTO book : books) {
            EntityModel<BookDTO> model = EntityModel.of(book);
            model.add(linkTo(methodOn(BookController.class).getOneBook(book.id())).withSelfRel());
            bookModels.add(model);
        }

        CollectionModel<EntityModel<BookDTO>> collectionModel = CollectionModel.of(
                bookModels,linkTo(methodOn(AuthorController.class).getAllBooksByAuthor(id, pageable)).withSelfRel());
        PageMetadata pageMetadata = new PageMetadata(
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages()
        );

        return ResponseEntity.ok(new PagedResponse<>(collectionModel, pageMetadata));
    }
}
