package com.github.maria_jansson.booksapi.service;

import com.github.maria_jansson.booksapi.dto.AuthorDTO;
import com.github.maria_jansson.booksapi.dto.BookDTO;
import com.github.maria_jansson.booksapi.exception.ResourceNotFoundException;
import com.github.maria_jansson.booksapi.model.Author;
import com.github.maria_jansson.booksapi.model.Book;
import com.github.maria_jansson.booksapi.repository.AuthorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthorService {
    private final AuthorRepository authorRepo;
    private final BookService bookService;

    public AuthorService (AuthorRepository authorRepo, BookService bookService) {
        this.authorRepo = authorRepo;
        this.bookService = bookService;
    }

    public Page<AuthorDTO> getAllAuthors(Optional<String> authorName, Pageable pageable) {
        Page<Author> authors;
        if (authorName.isPresent()) {
            authors = authorRepo.findByNameContainingIgnoreCase(authorName.get(), pageable);
        } else {
            authors = authorRepo.findAll(pageable);
        }
         return authors.map(this::authorToDTO);
    }

    public AuthorDTO getOneAuthor(Long id) {
        Author author = authorRepo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Author with id " + id + " not found."));
        return new AuthorDTO(author.getId(), author.getName());
    }

    public Page<BookDTO> getBooksByAuthor(Long id, Pageable pageable) {
        return bookService.getBooksByAuthor(id, pageable);
    }

    private AuthorDTO authorToDTO(Author author) {
        return new AuthorDTO(
                author.getId(),
                author.getName()
        );
    }
}