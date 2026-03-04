package com.github.maria_jansson.booksapi.service;

import com.github.maria_jansson.booksapi.dto.AuthorDTO;
import com.github.maria_jansson.booksapi.exception.ResourceNotFoundException;
import com.github.maria_jansson.booksapi.model.Author;
import com.github.maria_jansson.booksapi.repository.AuthorRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthorService {
    private final AuthorRepository authorRepo;

    public AuthorService (AuthorRepository authorRepo) {
        this.authorRepo = authorRepo;
    }

    public List<AuthorDTO> getAllAuthors() {
        List<Author> authors = authorRepo.findAll();
        List<AuthorDTO> authorDTOs = new ArrayList<>();
        for (Author author : authors) {
            AuthorDTO authorDto = new AuthorDTO(author.getId(), author.getName());
            authorDTOs.add(authorDto);
        }
        return authorDTOs;
    }

    public AuthorDTO getOneAuthor(Long id) {
        Author author = authorRepo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Author with id " + id + " not found."));
        return new AuthorDTO(author.getId(), author.getName());
    }
}
