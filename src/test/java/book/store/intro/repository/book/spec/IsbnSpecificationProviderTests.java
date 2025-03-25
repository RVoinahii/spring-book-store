package book.store.intro.repository.book.spec;

import static book.store.intro.repository.book.BookSpecificationBuilder.ISBN;
import static book.store.intro.util.TestBookDataUtil.BOOK_ISBN;
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
public class IsbnSpecificationProviderTests {
    @InjectMocks
    private IsbnSpecificationProvider isbnSpecificationProvider;

    @Mock
    private Root<Book> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private Path<String> isbnPath;

    @Mock
    private Predicate excpectedPredicate;

    @Test
    @DisplayName("""
            getKey():
             Should return the correct key for Isbn
            """)
    void getKey_returnsCorrectKey() {
        //Given
        String expectedKey = ISBN;

        //When
        String actualKey = isbnSpecificationProvider.getKey();

        //Then
        assertEquals(actualKey, expectedKey);
    }

    @Test
    @DisplayName("""
            getSpecification():
             Should return the correct Predicate for the isbn filter
            """)
    void getSpecification_returnsCorrectPredicate() {
        //Given
        String isbn = BOOK_ISBN;

        when(root.<String>get(ISBN)).thenReturn(isbnPath);
        when(isbnPath.in(isbn)).thenReturn(excpectedPredicate);

        //When
        Specification<Book> bookSpecification =
                isbnSpecificationProvider.getSpecification(isbn);
        Predicate actualPredicate = bookSpecification.toPredicate(root, query, criteriaBuilder);

        //Then
        assertNotNull(actualPredicate);
        assertThat(actualPredicate).isEqualTo(excpectedPredicate);
    }
}
