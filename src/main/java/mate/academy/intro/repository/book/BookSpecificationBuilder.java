package mate.academy.intro.repository.book;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import mate.academy.intro.dto.BookSearchParameters;
import mate.academy.intro.model.Book;
import mate.academy.intro.repository.SpecificationBuilder;
import mate.academy.intro.repository.SpecificationProviderManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    private static final String DELIMITER = "-";
    private static final String NULL_STRING = "null";

    private final SpecificationProviderManager<Book> bookSpecificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParameters searchParameters) {
        Specification<Book> spec = Specification.where(null);
        if (searchParameters.title() != null && !searchParameters.title().isEmpty()) {
            spec = spec.and(bookSpecificationProviderManager
                    .getSpecificationProvider("title")
                    .getSpecification(searchParameters.title()));
        }
        if (searchParameters.author() != null && !searchParameters.author().isEmpty()) {
            spec = spec.and(bookSpecificationProviderManager
                    .getSpecificationProvider("author")
                    .getSpecification(searchParameters.author()));
        }
        if (searchParameters.isbn() != null && !searchParameters.isbn().isEmpty()) {
            spec = spec.and(bookSpecificationProviderManager
                    .getSpecificationProvider("isbn")
                    .getSpecification(searchParameters.isbn()));
        }
        if (((searchParameters.bottomPrice() != null
                && searchParameters.bottomPrice().compareTo(BigDecimal.ZERO) > 0))
                || ((searchParameters.upperPrice() != null
                && searchParameters.upperPrice().compareTo(BigDecimal.ZERO) > 0))) {
            spec = spec.and(bookSpecificationProviderManager
                    .getSpecificationProvider("price")
                    .getSpecification(priceToStringConverter(searchParameters.bottomPrice(),
                            searchParameters.upperPrice())));
        }
        return spec;
    }

    private String priceToStringConverter(BigDecimal bottomPrice, BigDecimal upperPrice) {
        StringBuilder stringBuilder = new StringBuilder();
        return stringBuilder.append(bottomPrice != null
                        ? bottomPrice.toString()
                        : NULL_STRING)
                .append(DELIMITER)
                .append(upperPrice != null
                        ? upperPrice.toString()
                        : NULL_STRING)
                .toString();
    }
}
