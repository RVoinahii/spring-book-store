package mate.academy.intro.repository.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import mate.academy.intro.config.CustomMySqlContainer;
import mate.academy.intro.model.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookRepositoryTests {
    private static CustomMySqlContainer mySqlContainer = CustomMySqlContainer.getInstance();

    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 10;
    private static final Long VALID_ID_SAMPLE = 1L;
    private static final Long INVALID_ID_SAMPLE = 99L;

    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("""
            findAllByCategoryId():
             Should return a list with books when a valid category ID is provided
            """)
    @Sql(scripts = {
            "classpath:database/categories/insert_one_category.sql",
            "classpath:database/books/insert_one_book.sql",
            "classpath:database/books_categories/insert_book_category_relation_for_one_book.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/clear_database.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByCategoryId_ValidaCategoryId_ReturnsNonEmptyList() {
        //Given
        Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

        //When
        Page<Book> actualBooksPage = bookRepository.findAllByCategoryId(pageable, VALID_ID_SAMPLE);

        //Then
        assertEquals(1, actualBooksPage.getContent().size(),
                "Expected exactly one book to be returned for a valid category ID");
        assertEquals(1, actualBooksPage.getTotalElements(),
                "Total elements should be exactly 1");
        assertEquals(VALID_ID_SAMPLE, actualBooksPage.getContent().getFirst().getId(),
                "Returned book should have the expected category ID");
        assertEquals(PAGE_NUMBER, actualBooksPage.getNumber(),
                "Page number should match the requested page");
        assertEquals(PAGE_SIZE, actualBooksPage.getSize(),
                "Page size should match the requested size");
    }

    @Test
    @DisplayName("""
            findAllByCategoryId():
             Should return an empty list when an invalid category ID is provided
            """)
    void findAllByCategoryId_InvalidCategoryId_ReturnsEmptyPage() {
        //Given
        Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

        //When
        Page<Book> actualBooksPage = bookRepository.findAllByCategoryId(
                pageable, INVALID_ID_SAMPLE);

        //Then
        assertTrue(actualBooksPage.getContent().isEmpty(),
                "Expected empty result for invalid category ID");
        assertEquals(0, actualBooksPage.getTotalElements(),
                "Total elements should be 0 for an invalid category ID");
        assertEquals(PAGE_NUMBER, actualBooksPage.getNumber(),
                "Page number should match the requested page");
        assertEquals(PAGE_SIZE, actualBooksPage.getSize(),
                "Page size should match the requested size");
    }
}
