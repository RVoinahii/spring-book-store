package book.store.intro.repository.book.spec;

import static book.store.intro.repository.book.BookSpecificationBuilder.TITLE;
import static book.store.intro.repository.book.BookSpecificationBuilderTests.SOME_TITLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.eq;
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
public class TitleSpecificationProviderTests {
    @InjectMocks
    private TitleSpecificationProvider titleSpecificationProvider;

    @Mock
    private Root<Book> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private Path<String> authorPath;

    @Mock
    private Predicate excpectedPredicate;

    @Test
    @DisplayName("""
            getKey():
             Should return the correct key for Title
            """)
    void getKey_returnsCorrectKey() {
        //Given
        String expectedKey = TITLE;

        //When
        String actualKey = titleSpecificationProvider.getKey();

        //Then
        assertEquals(actualKey, expectedKey);
    }

    @Test
    @DisplayName("""
            getSpecification():
             Should return the correct Predicate for the title filter
            """)
    void getSpecification_returnsCorrectPredicate() {
        //Given
        String title = SOME_TITLE;

        when(root.<String>get(TITLE)).thenReturn(authorPath);
        when(criteriaBuilder.lower(authorPath)).thenReturn(authorPath);
        when(criteriaBuilder.like(eq(authorPath), anyString()
                .toLowerCase())).thenReturn(excpectedPredicate);

        //When
        Specification<Book> bookSpecification =
                titleSpecificationProvider.getSpecification(title);
        Predicate actualPredicate = bookSpecification.toPredicate(root, query, criteriaBuilder);

        //Then
        assertNotNull(actualPredicate);
        assertThat(actualPredicate).isEqualTo(excpectedPredicate);
    }
}
