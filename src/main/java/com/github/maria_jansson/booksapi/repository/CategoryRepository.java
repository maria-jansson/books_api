package com.github.maria_jansson.booksapi.repository;

import com.github.maria_jansson.booksapi.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long>  {
    Page<Category> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
