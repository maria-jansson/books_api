package com.github.maria_jansson.booksapi.repository;

import com.github.maria_jansson.booksapi.model.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    Page<Author> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
