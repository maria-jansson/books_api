package booksapi.service;

import booksapi.dto.AuthorDTO;
import booksapi.dto.BookDTO;
import booksapi.dto.BookRequestDTO;
import booksapi.dto.CategoryDTO;
import booksapi.exception.ResourceNotFoundException;
import booksapi.model.Author;
import booksapi.model.Book;
import booksapi.model.Category;
import booksapi.repository.AuthorRepository;
import booksapi.repository.BookRepository;
import booksapi.repository.CategoryRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service for managing book resources.
 * Handles CRUD operations and filtering for books.
 */
@Service
public class BookService {
  private final BookRepository bookRepo;
  private final AuthorRepository authorRepo;
  private final CategoryRepository categoryRepo;

  /**
   * Constructs a BookService with the required dependencies.
   *
   * @param bookRepo the repository for book data access
   * @param authorRepo the repository for author data access
   * @param categoryRepo the repository for category data access
   */
  public BookService(
      BookRepository bookRepo, AuthorRepository authorRepo, CategoryRepository categoryRepo) {
    this.bookRepo = bookRepo;
    this.authorRepo = authorRepo;
    this.categoryRepo = categoryRepo;
  }

  /**
   * Returns a paginated list of books, optionally filtered by author or category name.
   * If both parameters are added, only authorName is used.
   *
   * @param authorName optional filter for author name
   * @param categoryName optional filter for category name
   * @param pageable pagination parameters
   * @return a page of book DTOs
   */
  public Page<BookDTO> getAllBooks(
      Optional<String> authorName, Optional<String> categoryName, Pageable pageable) {
    Page<Book> books;
    // Note: only one filter is applied at a time - authorName takes precedence
    if (authorName.isPresent()) {
      books = bookRepo.findByAuthorsNameContainingIgnoreCase(authorName.get(), pageable);
    } else if (categoryName.isPresent()) {
      books = bookRepo.findByCategoriesNameContainingIgnoreCase(categoryName.get(), pageable);
    } else {
      books = bookRepo.findAll(pageable);
    }
    return books.map(this::bookToDTO);
  }

  /**
   * Returns a single book by ID.
   *
   * @param id the ID of the book to retrieve
   * @return the book DTO
   * @throws ResourceNotFoundException if no book with the given ID exists
   */
  public BookDTO getOneBook(Long id) {
    Book book =
        bookRepo
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Book with id " + id + " not found."));
    return bookToDTO(book);
  }

  /**
   * Returns a paginated list of books written by the specified author.
   *
   * @param id the ID of the author
   * @param pageable pagination parameters
   * @return a page of book DTOs
   * @throws ResourceNotFoundException if no author with the given ID exists
   */
  public Page<BookDTO> getBooksByAuthor(Long id, Pageable pageable) {
    authorRepo
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Author with id " + id + " not found."));
    Page<Book> books = bookRepo.findByAuthorsId(id, pageable);
    return books.map(this::bookToDTO);
  }

  /**
   * Returns a paginated list of books in the specified category.
   *
   * @param id the ID of the category
   * @param pageable pagination parameters
   * @return a page of book DTOs
   * @throws ResourceNotFoundException if no category with the given ID exists
   */
  public Page<BookDTO> getBooksByCategory(Long id, Pageable pageable) {
    categoryRepo
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Category with id " + id + " not found."));
    Page<Book> books = bookRepo.findByCategoriesId(id, pageable);
    return books.map(this::bookToDTO);
  }

  /**
   * Creates a new book from the given request data.
   *
   * @param data the request body containing book details
   * @return the created book DTO
   */
  public BookDTO createBook(BookRequestDTO data) {
    Book book = new Book();
    setBookFields(book, data);
    bookRepo.save(book);
    return bookToDTO(book);
  }

  /**
   * Updates an existing book by ID with the given request data.
   *
   * @param id the ID of the book to update
   * @param data the request body containing updated book details
   * @return the updated book DTO
   * @throws ResourceNotFoundException if no book with the given ID exists
   */
  public BookDTO updateBook(Long id, BookRequestDTO data) {
    Book book =
        bookRepo
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Book with id " + id + " not found."));
    setBookFields(book, data);
    bookRepo.save(book);
    return bookToDTO(book);
  }

  /**
   * Deletes a book by ID.
   *
   * @param id the ID of the book to delete
   * @throws ResourceNotFoundException if no book with the given ID exists
   */
  public void deleteBook(Long id) {
    bookRepo
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Book with id " + id + " not found."));
    bookRepo.deleteById(id);
  }

  /**
   * Sets the fields of a book entity from the given request data.
   *
   * @param book the book entity to update
   * @param data the request data containing the new field values
   */
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

  /**
   * Converts a Book entity to a BookDTO.
   *
   * @param book the book entity to convert
   * @return the corresponding BookDTO
   */
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
        book.getPublishYear());
  }

  /**
   * Converts a list of Author entities to a list of AuthorDTOs.
   *
   * @param authors the list of author entities to convert
   * @return the corresponding list of AuthorDTOs
   */
  private List<AuthorDTO> authorsToDTO(List<Author> authors) {
    List<AuthorDTO> authorDTOList = new ArrayList<>();
    for (Author author : authors) {
      AuthorDTO authorDTO = new AuthorDTO(author.getId(), author.getName());
      authorDTOList.add(authorDTO);
    }
    return authorDTOList;
  }

  /**
   * Converts a list of Category entities to a list of CategoryDTOs.
   *
   * @param categories the list of category entities to convert
   * @return the corresponding list of CategoryDTOs
   */
  private List<CategoryDTO> categoriesToDTO(List<Category> categories) {
    List<CategoryDTO> categoryDTOList = new ArrayList<>();
    for (Category category : categories) {
      CategoryDTO categoryDTO = new CategoryDTO(category.getId(), category.getName());
      categoryDTOList.add(categoryDTO);
    }
    return categoryDTOList;
  }
}
