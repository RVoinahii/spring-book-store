package mate.academy.intro.repository.book;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import mate.academy.intro.dto.book.BookSearchParameters;
import mate.academy.intro.model.Book;
import mate.academy.intro.repository.SpecificationProvider;
import mate.academy.intro.repository.SpecificationProviderManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
public class BookSpecificationBuilderTests {
    @Mock
    private SpecificationProviderManager<Book> bookSpecificationProviderManager;

    @Mock
    private SpecificationProvider<Book> titleSpecificationProvider;

    @Mock
    private SpecificationProvider<Book> authorSpecificationProvider;

    @Mock
    private SpecificationProvider<Book> isbnSpecificationProvider;

    @Mock
    private SpecificationProvider<Book> priceSpecificationProvider;

    private BookSpecificationBuilder bookSpecificationBuilder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookSpecificationBuilder = new BookSpecificationBuilder(bookSpecificationProviderManager);
    }

    @Test
    @DisplayName("""
            build():
             Should build specification for title when valid title is provided
            """)
    void build_ValidTitle_ShouldBuildSpecificationForTitle() {
        //Given
        String title = "Some title";
        BookSearchParameters searchParameters = new BookSearchParameters(
                title, null, null, null, null);
        Specification<Book> mockTitleSpec = mock(Specification.class);
        when(bookSpecificationProviderManager
                .getSpecificationProvider(BookSpecificationBuilder.TITLE))
                .thenReturn(titleSpecificationProvider);
        when(titleSpecificationProvider.getSpecification(title)).thenReturn(mockTitleSpec);

        //When
        Specification<Book> actualSpec = bookSpecificationBuilder.build(searchParameters);

        //Then
        assertNotNull(actualSpec);
        verify(bookSpecificationProviderManager)
                .getSpecificationProvider(BookSpecificationBuilder.TITLE);
        verify(titleSpecificationProvider).getSpecification(title);
    }

    @Test
    @DisplayName("""
            build():
             Should build specification for author when valid author is provided
            """)
    void build_ValidAuthor_ShouldBuildSpecificationForAuthor() {
        //Given
        String author = "Some Author";
        BookSearchParameters searchParameters = new BookSearchParameters(
                null, author, null, null, null);
        Specification<Book> mockAuthorSpec = mock(Specification.class);
        when(bookSpecificationProviderManager
                .getSpecificationProvider(BookSpecificationBuilder.AUTHOR))
                .thenReturn(authorSpecificationProvider);
        when(authorSpecificationProvider.getSpecification(author)).thenReturn(mockAuthorSpec);

        //When
        Specification<Book> actualSpec = bookSpecificationBuilder.build(searchParameters);

        //Then
        assertNotNull(actualSpec);
        verify(bookSpecificationProviderManager)
                .getSpecificationProvider(BookSpecificationBuilder.AUTHOR);
        verify(authorSpecificationProvider).getSpecification(author);
    }

    @Test
    @DisplayName("""
            build():
             Should build specification for ISBN when valid ISBN is provided
            """)
    void build_ValidIsbn_shouldBuildSpecificationForIsbn() {
        //Given
        String isbn = "978-3-16-148410-0";
        BookSearchParameters searchParameters = new BookSearchParameters(
                null, null, isbn, null, null);
        Specification<Book> mockIsbnSpec = mock(Specification.class);
        when(bookSpecificationProviderManager
                .getSpecificationProvider(BookSpecificationBuilder.ISBN))
                .thenReturn(isbnSpecificationProvider);
        when(isbnSpecificationProvider.getSpecification(isbn)).thenReturn(mockIsbnSpec);

        //When
        Specification<Book> actualSpec = bookSpecificationBuilder.build(searchParameters);

        //Then
        assertNotNull(actualSpec);
        verify(bookSpecificationProviderManager)
                .getSpecificationProvider(BookSpecificationBuilder.ISBN);
        verify(isbnSpecificationProvider).getSpecification(isbn);
    }

    @Test
    @DisplayName("""
            build():
             Should build specification for price range when
              both bottom and upper price are provided
            """)
    void build_BottomAndUpperPrice_ShouldBuildSpecificationForPrice() {
        //Given
        BigDecimal bottomPrice = new BigDecimal("10.00");
        BigDecimal upperPrice = new BigDecimal("100.00");
        BookSearchParameters searchParameters = new BookSearchParameters(
                null, null, null, bottomPrice, upperPrice);
        String priceRange = "10.00-100.00";
        Specification<Book> mockPriceSpec = mock(Specification.class);
        when(bookSpecificationProviderManager
                .getSpecificationProvider(BookSpecificationBuilder.PRICE))
                .thenReturn(priceSpecificationProvider);
        when(priceSpecificationProvider.getSpecification(priceRange)).thenReturn(mockPriceSpec);

        //When
        Specification<Book> actualSpec = bookSpecificationBuilder.build(searchParameters);

        //Then
        assertNotNull(actualSpec);
        verify(bookSpecificationProviderManager)
                .getSpecificationProvider(BookSpecificationBuilder.PRICE);
        verify(priceSpecificationProvider).getSpecification(priceRange);
    }

    @Test
    @DisplayName("""
            build():
             Should build specification for price range with only bottom price provided
            """)
    void build_OnlyBottomPrice_ShouldBuildSpecificationForPrice() {
        //Given
        BigDecimal bottomPrice = new BigDecimal("10.00");
        BookSearchParameters searchParameters = new BookSearchParameters(
                null, null, null, bottomPrice, null);
        String priceRange = "10.00-no_value";
        Specification<Book> mockPriceSpec = mock(Specification.class);
        when(bookSpecificationProviderManager
                .getSpecificationProvider(BookSpecificationBuilder.PRICE))
                .thenReturn(priceSpecificationProvider);
        when(priceSpecificationProvider.getSpecification(priceRange)).thenReturn(mockPriceSpec);

        //When
        Specification<Book> actualSpec = bookSpecificationBuilder.build(searchParameters);

        //Then
        assertNotNull(actualSpec);
        verify(bookSpecificationProviderManager)
                .getSpecificationProvider(BookSpecificationBuilder.PRICE);
        verify(priceSpecificationProvider).getSpecification(priceRange);
    }

    @Test
    @DisplayName("""
            build():
             Should build specification for price range with only upper price provided
            """)
    void build_OnlyUpperPrice_ShouldBuildSpecificationForPrice() {
        //Given
        BigDecimal upperPrice = new BigDecimal("100.00");
        BookSearchParameters searchParameters = new BookSearchParameters(
                null, null, null, null, upperPrice);
        String priceRange = "no_value-100.00";
        Specification<Book> mockPriceSpec = mock(Specification.class);
        when(bookSpecificationProviderManager
                .getSpecificationProvider(BookSpecificationBuilder.PRICE))
                .thenReturn(priceSpecificationProvider);
        when(priceSpecificationProvider.getSpecification(priceRange)).thenReturn(mockPriceSpec);

        //When
        Specification<Book> actualSpec = bookSpecificationBuilder.build(searchParameters);

        //Then
        assertNotNull(actualSpec);
        verify(bookSpecificationProviderManager)
                .getSpecificationProvider(BookSpecificationBuilder.PRICE);
        verify(priceSpecificationProvider).getSpecification(priceRange);
    }

    @Test
    @DisplayName("""
            build():
             Should return empty specification when no parameters are provided
            """)
    void build_InvalidParameters_ShouldReturnEmptySpecification() {
        //Given
        BookSearchParameters searchParameters = new BookSearchParameters(
                null, null, null, null, null);

        //When
        Specification<Book> actualSpec = bookSpecificationBuilder.build(searchParameters);

        //Then
        assertNotNull(actualSpec);
    }

    @Test
    @DisplayName("""
            build():
             Should return empty specification when empty and negative parameters are provided
            """)
    void build_EmptyAndNegativeParameters_ShouldReturnEmptySpecification() {
        //Given
        BigDecimal bottomPrice = new BigDecimal("-1.00");
        BigDecimal upperPrice = new BigDecimal("-1.00");
        BookSearchParameters searchParameters = new BookSearchParameters(
                "", "", "", bottomPrice, upperPrice);

        //When
        Specification<Book> actualSpec = bookSpecificationBuilder.build(searchParameters);

        //Then
        assertNotNull(actualSpec);
    }
}
