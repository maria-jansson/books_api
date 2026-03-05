package com.github.maria_jansson.booksapi.dto;

public record PageMetadata(
        int currentPage,
        int pageSize,
        long totalElements,
        int totalPages
) {
}
