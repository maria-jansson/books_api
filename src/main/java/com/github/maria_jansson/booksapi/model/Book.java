package com.github.maria_jansson.booksapi.model;

import com.github.maria_jansson.booksapi.dto.AuthorDTO;
import com.github.maria_jansson.booksapi.dto.CategoryDTO;
import jakarta.persistence.*;

import java.util.List;

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

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public String getDescription() {
        return description;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public String getPublisher() {
        return publisher;
    }

    public Double getPrice() {
        return price;
    }

    public String getPublishMonth() {
        return publishMonth;
    }

    public int getPublishYear() {
        return publishYear;
    }

    public void setTitle(String title) {
    }

    public void setAuthors(List<Author> longs) {
    }

    public void setDescription(String description) {
    }

    public void setCategories(List<Category> longs) {
    }

    public void setPublisher(String publisher) {
    }

    public void setPrice(Double price) {
    }

    public void setPublishMonth(String s) {
    }

    public void setPublishYear(int i) {
    }

}
