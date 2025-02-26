package mate.academy.intro.repository.book;

import static mate.academy.intro.repository.book.BookSpecificationBuilder.AUTHOR;
import static mate.academy.intro.repository.book.BookSpecificationBuilder.ISBN;
import static mate.academy.intro.repository.book.BookSpecificationBuilder.PRICE;
import static mate.academy.intro.repository.book.BookSpecificationBuilder.TITLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;

import java.util.List;
import mate.academy.intro.exceptions.SpecificationNotFoundException;
import mate.academy.intro.model.Book;
import mate.academy.intro.repository.SpecificationProvider;
import mate.academy.intro.repository.book.spec.AuthorSpecificationProvider;
import mate.academy.intro.repository.book.spec.IsbnSpecificationProvider;
import mate.academy.intro.repository.book.spec.PriceSpecificationProvider;
import mate.academy.intro.repository.book.spec.TitleSpecificationProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BookSpecificationProviderManagerTests {
    @Mock
    private AuthorSpecificationProvider authorSpecificationProvider;

    @Mock
    private IsbnSpecificationProvider isbnSpecificationProvider;

    @Mock
    private PriceSpecificationProvider priceSpecificationProvider;

    @Mock
    private TitleSpecificationProvider titleSpecificationProvider;

    @Mock
    private BookSpecificationProviderManager bookSpecificationProviderManager;

    @BeforeEach
    void setUp() {
        lenient().when(authorSpecificationProvider.getKey()).thenReturn(AUTHOR);
        lenient().when(isbnSpecificationProvider.getKey()).thenReturn(ISBN);
        lenient().when(priceSpecificationProvider.getKey()).thenReturn(PRICE);
        lenient().when(titleSpecificationProvider.getKey()).thenReturn(TITLE);
        bookSpecificationProviderManager = new BookSpecificationProviderManager(
                List.of(authorSpecificationProvider, isbnSpecificationProvider,
                        priceSpecificationProvider, titleSpecificationProvider)
        );
    }

    @Test
    @DisplayName("""
            getSpecificationProvider():
             Valid key "author" should return the corresponding provider
            """)
    void getSpecificationProvider_validKeyAuthor_returnsProvider() {
        //Given
        String expectedKey = AUTHOR;

        //When
        SpecificationProvider<Book> specificationProvider =
                bookSpecificationProviderManager.getSpecificationProvider(expectedKey);

        //Then
        assertNotNull(specificationProvider);
        assertThat(expectedKey).isEqualTo(specificationProvider.getKey());
    }

    @Test
    @DisplayName("""
            getSpecificationProvider():
             Valid key "isbn" should return the corresponding provider
            """)
    void getSpecificationProvider_validKeyIsbn_returnsProvider() {
        //Given
        String expectedKey = ISBN;

        //When
        SpecificationProvider<Book> specificationProvider =
                bookSpecificationProviderManager.getSpecificationProvider(expectedKey);

        //Then
        assertNotNull(specificationProvider);
        assertThat(expectedKey).isEqualTo(specificationProvider.getKey());
    }

    @Test
    @DisplayName("""
            getSpecificationProvider():
             Valid key "price" should return the corresponding provider
            """)
    void getSpecificationProvider_validKeyPrice_returnsProvider() {
        //Given
        String expectedKey = PRICE;

        //When
        SpecificationProvider<Book> specificationProvider =
                bookSpecificationProviderManager.getSpecificationProvider(expectedKey);

        //Then
        assertNotNull(specificationProvider);
        assertThat(expectedKey).isEqualTo(specificationProvider.getKey());
    }

    @Test
    @DisplayName("""
            getSpecificationProvider():
             Valid key "title" should return the corresponding provider
            """)
    void getSpecificationProvider_validKeyTitle_returnsProvider() {
        //Given
        String expectedKey = TITLE;

        //When
        SpecificationProvider<Book> specificationProvider =
                bookSpecificationProviderManager.getSpecificationProvider(expectedKey);

        //Then
        assertNotNull(specificationProvider);
        assertThat(expectedKey).isEqualTo(specificationProvider.getKey());
    }

    @Test
    @DisplayName("""
            getSpecificationProvider():
             Invalid key should throw an exception
            """)
    void getSpecificationProvider_invalidKey_throwsException() {
        //Given
        String key = "invalid_key";

        //When
        Exception exception = assertThrows(
                SpecificationNotFoundException.class, () ->
                        bookSpecificationProviderManager.getSpecificationProvider(key)
        );

        //Then
        String expected = "Can't find correct specification provided for key " + key;
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }
}
