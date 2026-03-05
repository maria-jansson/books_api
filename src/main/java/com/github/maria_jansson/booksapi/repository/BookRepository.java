package com.github.maria_jansson.booksapi.repository;

import com.github.maria_jansson.booksapi.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;



public interface BookRepository extends JpaRepository<Book, Long> {
    Page<Book> findByAuthorsId(Long id, Pageable pageable);
    Page<Book> findByCategoriesId(Long id, Pageable pageable);
    Page<Book> findByAuthorsNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Book> findByCategoriesNameContainingIgnoreCase(String name, Pageable pageable);
}
