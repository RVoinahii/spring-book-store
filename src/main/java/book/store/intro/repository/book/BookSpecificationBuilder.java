package book.store.intro.repository.book;

import book.store.intro.dto.book.BookSearchParameters;
import book.store.intro.model.Book;
import book.store.intro.repository.SpecificationBuilder;
import book.store.intro.repository.SpecificationProviderManager;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    public static final String TITLE = "title";
    public static final String AUTHOR = "author";
    public static final String ISBN = "isbn";
    public static final String PRICE = "price";
    public static final String NO_VALUE = "no_value";
    public static final String DELIMITER = "-";

    private final SpecificationProviderManager<Book> bookSpecificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParameters searchParameters) {
        Specification<Book> spec = Specification.where(null);
        if (searchParameters.title() != null && !searchParameters.title().isEmpty()) {
            spec = spec.and(bookSpecificationProviderManager
                    .getSpecificationProvider(TITLE)
                    .getSpecification(searchParameters.title()));
        }
        if (searchParameters.author() != null && !searchParameters.author().isEmpty()) {
            spec = spec.and(bookSpecificationProviderManager
                    .getSpecificationProvider(AUTHOR)
                    .getSpecification(searchParameters.author()));
        }
        if (searchParameters.isbn() != null && !searchParameters.isbn().isEmpty()) {
            spec = spec.and(bookSpecificationProviderManager
                    .getSpecificationProvider(ISBN)
                    .getSpecification(searchParameters.isbn()));
        }
        if (((searchParameters.bottomPrice() != null
                && searchParameters.bottomPrice().compareTo(BigDecimal.ZERO) > 0))
                || ((searchParameters.upperPrice() != null
                && searchParameters.upperPrice().compareTo(BigDecimal.ZERO) > 0))) {
            spec = spec.and(bookSpecificationProviderManager
                    .getSpecificationProvider(PRICE)
                    .getSpecification(priceToStringConverter(searchParameters.bottomPrice(),
                            searchParameters.upperPrice())));
        }
        return spec;
    }

    private String priceToStringConverter(BigDecimal bottomPrice, BigDecimal upperPrice) {
        StringBuilder stringBuilder = new StringBuilder();
        return stringBuilder.append(bottomPrice != null
                        ? bottomPrice.toString()
                        : NO_VALUE)
                .append(DELIMITER)
                .append(upperPrice != null
                        ? upperPrice.toString()
                        : NO_VALUE)
                .toString();
    }
}
