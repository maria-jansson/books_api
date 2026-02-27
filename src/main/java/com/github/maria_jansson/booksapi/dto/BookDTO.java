package com.github.maria_jansson.booksapi.dto;

public record BookDTO(
        Long id,
        String title,
        AuthorDTO author,
        String description,
        CategoryDTO category,
        String publisher,
        Double price,
        String publishMonth,
        int publishYear) {
}
