package booksapi.util;

import booksapi.model.Author;
import booksapi.model.Book;
import booksapi.model.Category;
import booksapi.repository.AuthorRepository;
import booksapi.repository.BookRepository;
import booksapi.repository.CategoryRepository;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DataLoader implements CommandLineRunner {
  private static final String CSV_FILE = "BooksDatasetClean.csv";

  private final BookRepository bookRepo;
  private final AuthorRepository authorRepo;
  private final CategoryRepository categoryRepo;

  public DataLoader(
          BookRepository bookRepo,
          AuthorRepository authorRepo,
          CategoryRepository categoryRepo) {
    this.bookRepo = bookRepo;
    this.authorRepo = authorRepo;
    this.categoryRepo = categoryRepo;
  }

  @Override
  public void run(String @NonNull ... args) throws Exception {
    if (bookRepo.count() > 0) {
      return;
    }

    Map<String, Author> authorMap = new HashMap<>();
    Map<String, Category> categoryMap = new HashMap<>();
    List<Book> books = new ArrayList<>();

    try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CSV_FILE)) {
      if (inputStream == null) {
        throw new IllegalStateException("CSV file not found: " + CSV_FILE);
      }
      try (BufferedReader reader = new BufferedReader(
              new InputStreamReader(inputStream, StandardCharsets.UTF_8));
           CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder()
                   .setHeader()
                   .setSkipHeaderRecord(true)
                   .setTrim(true)
                   .build())) {

        for (CSVRecord record : csvParser) {
          String title = record.get("Title");
          String authorsRaw = record.get("Authors");
          String description = record.get("Description");
          String categoryRaw = record.get("Category");
          String publisher = record.get("Publisher");
          String priceRaw = record.get("Price Starting With ($)");
          String publishMonth = record.get("Publish Date (Month)");
          String publishYearRaw = record.get("Publish Date (Year)");

          Author author = getOrCreateAuthor(authorsRaw, authorMap);
          List<Category> categories = getOrCreateCategories(categoryRaw, categoryMap);

          Book book = new Book();
          book.setTitle(title);
          book.setAuthors(List.of(author));
          book.setDescription(description);
          book.setCategories(categories);
          book.setPublisher(publisher);
          book.setPrice(parseDouble(priceRaw));
          book.setPublishMonth(publishMonth);
          book.setPublishYear(parseInt(publishYearRaw));
          books.add(book);
        }
      }
    }

    authorRepo.saveAll(authorMap.values());
    categoryRepo.saveAll(categoryMap.values());
    bookRepo.saveAll(books);
  }

  private Author getOrCreateAuthor(String authorsRaw, Map<String, Author> authorMap) {
    String cleanName = authorsRaw.replaceAll("(?i)^By\\s+", "");
    return authorMap.computeIfAbsent(cleanName, name -> {
      Author author = new Author();
      author.setName(name);
      return author;
    });
  }

  private List<Category> getOrCreateCategories(
          String categoryRaw,
          Map<String, Category> categoryMap) {
    String[] categoryNames = categoryRaw.split(" , ");
    List<Category> categories = new ArrayList<>();
    for (String categoryName : categoryNames) {
      Category category = categoryMap.computeIfAbsent(categoryName, name -> {
        Category c = new Category();
        c.setName(name);
        return c;
      });
      categories.add(category);
    }
    return categories;
  }

  private double parseDouble(String value) {
    try {
      return Double.parseDouble(value);
    } catch (NumberFormatException e) {
      return 0.0;
    }
  }

  private int parseInt(String value) {
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      return 0;
    }
  }
}