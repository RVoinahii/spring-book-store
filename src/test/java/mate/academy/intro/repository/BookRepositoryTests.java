package mate.academy.intro.repository;

import mate.academy.intro.config.CustomMySqlContainer;
import mate.academy.intro.model.Book;
import mate.academy.intro.model.Category;
import mate.academy.intro.repository.book.BookRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookRepositoryTests {

    public static CustomMySqlContainer mySqlContainer = CustomMySqlContainer.getInstance();

    private static final Long BOOK_ID = 1L;
    private static final String BOOK_TITLE = "Book1";
    private static final String BOOK_AUTHOR = "Author1";
    private static final String BOOK_ISBN = "978-3-16-148410-0";
    private static final BigDecimal BOOK_PRICE = BigDecimal.valueOf(39.99);

    @Autowired
    private BookRepository bookRepository;

    @Test
    void findAllByCategoryId_ValidaCategoryId_ReturnsNonEmptyList() {
        mySqlContainer.start();
        Book book = createBookSample(BOOK_ID);
        bookRepository.save(book);

        List<Book> actual = bookRepository.findAll();

        Assertions.assertEquals(1, actual.size());
        mySqlContainer.stop();
    }

    private Book createBookSample(Long id) {
        Book book = new Book();
        book.setId(id);
        book.setTitle(BOOK_TITLE);
        book.setAuthor(BOOK_AUTHOR);
        book.setIsbn(BOOK_ISBN);
        book.setPrice(BOOK_PRICE);

        Category category = new Category();
        category.setId(id);
        category.setName("Category1");

        book.setCategories(Set.of(category));
        return book;
    }
}
