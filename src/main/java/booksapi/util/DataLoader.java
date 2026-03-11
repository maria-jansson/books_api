package booksapi.util;

import booksapi.model.Author;
import booksapi.model.Book;
import booksapi.model.Category;
import booksapi.repository.AuthorRepository;
import booksapi.repository.BookRepository;
import booksapi.repository.CategoryRepository;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Component that loads book data from a CSV file into the database on startup.
 * Only runs if the database is empty to avoid duplicate data.
 */
@Component
public class DataLoader implements CommandLineRunner {
  private static final String CSV_FILE = "BooksDatasetClean.csv";

  private final BookRepository bookRepo;
  private final AuthorRepository authorRepo;
  private final CategoryRepository categoryRepo;

  /**
   * Constructs a DataLoader with the required repositories.
   *
   * @param bookRepo the repository for book data access
   * @param authorRepo the repository for author data access
   * @param categoryRepo the repository for category data access
   */
  public DataLoader(
      BookRepository bookRepo, AuthorRepository authorRepo, CategoryRepository categoryRepo) {
    this.bookRepo = bookRepo;
    this.authorRepo = authorRepo;
    this.categoryRepo = categoryRepo;
  }

  /**
   * Loads book data from the CSV file into the database if the database is empty.
   *
   * @param args command line arguments
   * @throws Exception if the CSV file cannot be found or parsed
   */
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
      try (BufferedReader reader =
              new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
          CSVParser csvParser =
              new CSVParser(
                  reader,
                  CSVFormat.DEFAULT
                      .builder()
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

  /**
   * Returns an existing author from the map or creates a new one if not present.
   *
   * @param authorsRaw the raw author string from the CSV
   * @param authorMap the map of existing authors keyed by name
   * @return the existing or newly created author
   */
  private Author getOrCreateAuthor(String authorsRaw, Map<String, Author> authorMap) {
    // Remove "By " prefix that some author names have in the dataset
    String cleanName = authorsRaw.replaceAll("(?i)^By\\s+", "");
    return authorMap.computeIfAbsent(
        cleanName,
        name -> {
          Author author = new Author();
          author.setName(name);
          return author;
        });
  }

  /**
   * Returns a list of categories from the map, creating new ones as needed.
   *
   * @param categoryRaw the raw category string from the CSV
   * @param categoryMap the map of existing categories keyed by name
   * @return the list of existing or newly created categories
   */
  private List<Category> getOrCreateCategories(
      String categoryRaw, Map<String, Category> categoryMap) {
    String[] categoryNames = categoryRaw.split(" , ");
    List<Category> categories = new ArrayList<>();
    for (String categoryName : categoryNames) {
      Category category =
          categoryMap.computeIfAbsent(
              categoryName,
              name -> {
                Category c = new Category();
                c.setName(name);
                return c;
              });
      categories.add(category);
    }
    return categories;
  }

  /**
   * Parses a string to a double, returning 0.0 if parsing fails.
   *
   * @param value the string to parse
   * @return the parsed double value, or 0.0 if invalid
   */
  private double parseDouble(String value) {
    try {
      return Double.parseDouble(value);
    } catch (NumberFormatException e) {
      return 0.0;
    }
  }

  /**
   * Parses a string to an int, returning 0 if parsing fails.
   *
   * @param value the string to parse
   * @return the parsed int value, or 0 if invalid
   */
  private int parseInt(String value) {
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      return 0;
    }
  }
}
