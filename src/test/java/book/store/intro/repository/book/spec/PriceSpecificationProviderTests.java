package book.store.intro.repository.book.spec;

import static book.store.intro.repository.book.BookSpecificationBuilder.PRICE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import book.store.intro.model.Book;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
public class PriceSpecificationProviderTests {
    private static final String PRICE_SAMPLE = "9.99-29.99";
    private static final String ONLY_BOTTOM = "9.99-no_value";
    private static final String ONLY_UPPER = "no_value-29.99";

    @InjectMocks
    private PriceSpecificationProvider priceSpecificationProvider;

    @Mock
    private Root<Book> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private Path<String> pricePath;

    @Mock
    private Predicate excpectedPredicate;

    @Test
    @DisplayName("""
            getKey():
             Should return the correct key for Price
            """)
    void getKey_returnsCorrectKey() {
        //Given
        String expectedKey = PRICE;

        //When
        String actualKey = priceSpecificationProvider.getKey();

        //Then
        assertEquals(actualKey, expectedKey);
    }

    @Test
    @DisplayName("""
            getSpecification():
             Should return the correct Predicate when only upper price is provided
            """)
    void getSpecification_OnlyUpperPrice_returnsCorrectPredicate() {
        //Given
        String upperPrice = "29.99";

        when(root.<String>get(PRICE)).thenReturn(pricePath);
        when(criteriaBuilder.lessThan(pricePath, upperPrice))
                .thenReturn(excpectedPredicate);

        //When
        Specification<Book> bookSpecification =
                priceSpecificationProvider.getSpecification(ONLY_UPPER);
        Predicate actualPredicate = bookSpecification.toPredicate(root, query, criteriaBuilder);

        //Then
        assertThat(actualPredicate).isEqualTo(excpectedPredicate);
    }

    @Test
    @DisplayName("""
            getSpecification():
             Should return the correct Predicate for the price range filter
            """)
    void getSpecification_BottomAndUpperPrice_returnsCorrectPredicate() {
        //Given
        String bottomPrice = "9.99";
        String upperPrice = "29.99";

        when(root.<String>get(PRICE)).thenReturn(pricePath);
        when(criteriaBuilder.between(pricePath, bottomPrice, upperPrice))
                .thenReturn(excpectedPredicate);

        //When
        Specification<Book> bookSpecification =
                priceSpecificationProvider.getSpecification(PRICE_SAMPLE);
        Predicate actualPredicate = bookSpecification.toPredicate(root, query, criteriaBuilder);

        //Then
        assertThat(actualPredicate).isEqualTo(excpectedPredicate);
    }

    @Test
    @DisplayName("""
            getSpecification():
             Should return the correct Predicate when only bottom price is provided
            """)
    void getSpecification_OnlyBottomPrice_returnsCorrectPredicate() {
        //Given
        String bottomPrice = "9.99";

        when(root.<String>get(PRICE)).thenReturn(pricePath);
        when(criteriaBuilder.greaterThan(pricePath, bottomPrice))
                .thenReturn(excpectedPredicate);

        //When
        Specification<Book> bookSpecification =
                priceSpecificationProvider.getSpecification(ONLY_BOTTOM);
        Predicate actualPredicate = bookSpecification.toPredicate(root, query, criteriaBuilder);

        //Then
        assertNotNull(actualPredicate);
        assertThat(actualPredicate).isEqualTo(excpectedPredicate);
    }
}
