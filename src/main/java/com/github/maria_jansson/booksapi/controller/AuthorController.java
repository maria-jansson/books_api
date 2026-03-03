package com.github.maria_jansson.booksapi.controller;

import com.github.maria_jansson.booksapi.dto.AuthorDTO;
import com.github.maria_jansson.booksapi.service.AuthorService;
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
    public ResponseEntity<CollectionModel<EntityModel<AuthorDTO>>> getAllAuthors() {
        List<AuthorDTO> authors = authorService.getAllAuthors();
        List<EntityModel<AuthorDTO>> authorModels = new ArrayList<>();
        for (AuthorDTO author : authors) {
            EntityModel<AuthorDTO> model = EntityModel.of(author);
            model.add(linkTo(methodOn(AuthorController.class).getOneAuthor(author.id())).withSelfRel());
            authorModels.add(model);
        }
        return ResponseEntity.ok(CollectionModel.of(authorModels,
                linkTo(methodOn(AuthorController.class).getAllAuthors()).withSelfRel()));
    }
}
