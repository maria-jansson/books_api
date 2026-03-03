package com.github.maria_jansson.booksapi.model;

import com.github.maria_jansson.booksapi.dto.AuthorDTO;
import com.github.maria_jansson.booksapi.dto.CategoryDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Book {
    @Id
    @GeneratedValue
    private Long id;
    private String title;
    @ManyToMany
    private List<Author> authors;
    private String description;
    @ManyToMany
    private List<Category> categories;
    private String publisher;
    private double price;
    private String publishMonth;
    private int publishYear;

}
