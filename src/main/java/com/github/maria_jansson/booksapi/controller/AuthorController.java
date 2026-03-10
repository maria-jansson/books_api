package com.github.maria_jansson.booksapi.controller;

import com.github.maria_jansson.booksapi.dto.AuthorDTO;

import com.github.maria_jansson.booksapi.dto.BookDTO;
import com.github.maria_jansson.booksapi.dto.PageMetadata;
import com.github.maria_jansson.booksapi.dto.PagedResponse;
import com.github.maria_jansson.booksapi.service.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.Link;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
@RequestMapping(value = "/api/v1/authors", produces = "application/json")
public class AuthorController {
    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @Operation(summary = "Get author by ID", description = "Returns a single author with HATEOAS links.")
    @GetMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "Returns a single author")
    @ApiResponse(responseCode = "404", description = "Author not found")
    public ResponseEntity<EntityModel<AuthorDTO>> getOneAuthor(@PathVariable Long id) {
        AuthorDTO authorDTO = authorService.getOneAuthor(id);
        EntityModel<AuthorDTO> model = EntityModel.of(authorDTO);
        model.add(linkTo(methodOn(AuthorController.class).getOneAuthor(id)).withSelfRel());

        return ResponseEntity.ok(model);
    }

    @Operation(summary = "Get all authors", description = "Returns a paginated list of authors. Filter by authorName.")
    @GetMapping
    @ApiResponse(responseCode = "200", description = "Returns paginated list of authors")
    public ResponseEntity<PagedResponse<EntityModel<AuthorDTO>>> getAllAuthors(@RequestParam Optional<String> authorName, Pageable pageable) {
        Page<AuthorDTO> authors = authorService.getAllAuthors(authorName, pageable);
        List<EntityModel<AuthorDTO>> authorModels = new ArrayList<>();

        for (AuthorDTO author : authors) {
            EntityModel<AuthorDTO> model = EntityModel.of(author);
            model.add(linkTo(methodOn(AuthorController.class).getOneAuthor(author.id())).withSelfRel());
            authorModels.add(model);
        }

        // Build self link from actual request URL to avoid HATEOAS generating a URL template
        String selfLink = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .toUriString();

        CollectionModel<EntityModel<AuthorDTO>> collectionModel = CollectionModel.of(
            authorModels,
            Link.of(selfLink).withSelfRel()
        );

        PageMetadata pageMetadata = new PageMetadata(
            authors.getNumber(),
            authors.getSize(),
            authors.getTotalElements(),
            authors.getTotalPages()
        );

        return ResponseEntity.ok(new PagedResponse<>(collectionModel, pageMetadata));
    }

    @Operation(summary = "Get books by author", description = "Returns a paginated list of books written by the specified author.")
    @GetMapping("/{id}/books")
    @ApiResponse(responseCode = "200", description = "Returns books by author")
    @ApiResponse(responseCode = "404", description = "Author not found")
    public ResponseEntity<PagedResponse<EntityModel<BookDTO>>> getAllBooksByAuthor(@PathVariable Long id, Pageable pageable) {
        Page<BookDTO> books = authorService.getBooksByAuthor(id, pageable);
        List<EntityModel<BookDTO>> bookModels = new ArrayList<>();

        for (BookDTO book : books) {
            EntityModel<BookDTO> model = EntityModel.of(book);
            model.add(linkTo(methodOn(BookController.class).getOneBook(book.id())).withSelfRel());
            bookModels.add(model);
        }

        // Build self link from actual request URL to avoid HATEOAS generating a URL template
        String selfLink = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .toUriString();

        CollectionModel<EntityModel<BookDTO>> collectionModel = CollectionModel.of(
            bookModels,
            Link.of(selfLink).withSelfRel()
        );

        PageMetadata pageMetadata = new PageMetadata(
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages()
        );

        return ResponseEntity.ok(new PagedResponse<>(collectionModel, pageMetadata));
    }
}
