package com.github.maria_jansson.booksapi.service;

import com.github.maria_jansson.booksapi.dto.AuthorDTO;
import com.github.maria_jansson.booksapi.dto.BookDTO;
import com.github.maria_jansson.booksapi.dto.BookRequestDTO;
import com.github.maria_jansson.booksapi.dto.CategoryDTO;
import com.github.maria_jansson.booksapi.exception.ResourceNotFoundException;
import com.github.maria_jansson.booksapi.model.Author;
import com.github.maria_jansson.booksapi.model.Book;
import com.github.maria_jansson.booksapi.model.Category;
import com.github.maria_jansson.booksapi.repository.AuthorRepository;
import com.github.maria_jansson.booksapi.repository.BookRepository;
import com.github.maria_jansson.booksapi.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {
    private final BookRepository bookRepo;
    private final AuthorRepository authorRepo;
    private final CategoryRepository categoryRepo;

    public BookService (BookRepository bookRepo, AuthorRepository authorRepo, CategoryRepository categoryRepo) {
        this.bookRepo = bookRepo;
        this.authorRepo = authorRepo;
        this.categoryRepo = categoryRepo;
    }

    public List<BookDTO> getAllBooks() {
        List<Book> books = bookRepo.findAll();
        List<BookDTO> bookDTOs = new ArrayList<>();
        for (Book book : books) {
            BookDTO bookDTO = bookToDTO(book);
            bookDTOs.add(bookDTO);
        }
        return bookDTOs;
    }

    public BookDTO getOneBook(Long id) {
        Book book = bookRepo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Book with id " + id + " not found."));
        return bookToDTO(book);
    }

    public BookDTO createBook(BookRequestDTO data) {
        Book book = new Book();
        setBookFields(book, data);
        bookRepo.save(book);
        return bookToDTO(book);
    }

    public BookDTO updateBook(Long id, BookRequestDTO data) {
        Book book = bookRepo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Book with id " + id + " not found."));
        setBookFields(book, data);
        bookRepo.save(book);
        return bookToDTO(book);
    }

    public void deleteBook(Long id) {
        bookRepo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Book with id " + id + " not found."));
        bookRepo.deleteById(id);
    }

    private void setBookFields(Book book, BookRequestDTO data) {
        List<Author> authors = authorRepo.findAllById(data.authorIds());
        List<Category> categories = categoryRepo.findAllById(data.categoryIds());
        book.setTitle(data.title());
        book.setAuthors(authors);
        book.setDescription(data.description());
        book.setCategories(categories);
        book.setPublisher(data.publisher());
        book.setPrice(data.price());
        book.setPublishMonth(data.publishMonth());
        book.setPublishYear(data.publishYear());
    }

    private BookDTO bookToDTO(Book book) {
        return new BookDTO(
            book.getId(),
            book.getTitle(),
            authorsToDTO(book.getAuthors()),
            book.getDescription(),
            categoriesToDTO(book.getCategories()),
            book.getPublisher(),
            book.getPrice(),
            book.getPublishMonth(),
            book.getPublishYear()
        );
    }

    private List<AuthorDTO> authorsToDTO (List<Author> authors) {
        List<AuthorDTO> authorDTOList = new ArrayList<>();
        for (Author author : authors) {
            AuthorDTO authorDTO = new AuthorDTO(
                    author.getId(),
                    author.getName()
            );
            authorDTOList.add(authorDTO);
        }
        return authorDTOList;
    }

    private List<CategoryDTO> categoriesToDTO (List<Category> categories) {
        List<CategoryDTO> categoryDTOList = new ArrayList<>();
        for (Category category : categories) {
            CategoryDTO categoryDTO = new CategoryDTO(
                    category.getId(),
                    category.getName()
            );
            categoryDTOList.add(categoryDTO);
        }
        return categoryDTOList;
    }
}
