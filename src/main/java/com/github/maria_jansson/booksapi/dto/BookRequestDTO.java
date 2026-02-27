package com.github.maria_jansson.booksapi.dto;

public record BookRequestDTO(
        String title,
        Long authorId,
        String description,
        Long categoryId,
        String publisher,
        Double price,
        String publishMonth,
        int publishYear
) {
}
