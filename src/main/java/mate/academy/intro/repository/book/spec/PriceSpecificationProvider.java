package mate.academy.intro.repository.book.spec;

import mate.academy.intro.model.Book;
import mate.academy.intro.repository.SpecificationProvider;
import mate.academy.intro.repository.book.BookSpecificationConstants;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class PriceSpecificationProvider implements SpecificationProvider<Book> {
    private static final String DELIMITER = "-";
    private static final int BOTTOM_PRICE = 0;
    private static final int UPPER_PRICE = 1;
    private static final String NO_VALUE = "no_value";

    @Override
    public String getKey() {
        return BookSpecificationConstants.PRICE;
    }

    @Override
    public Specification<Book> getSpecification(String params) {
        String[] paramsSplit = params.split(DELIMITER);
        String bottomPrice = paramsSplit[BOTTOM_PRICE];
        String upperPrice = paramsSplit[UPPER_PRICE];

        if (bottomPrice.equals(NO_VALUE)) {
            return (root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThan(
                            root.get(BookSpecificationConstants.PRICE), upperPrice
                    );
        }

        if (upperPrice.equals(NO_VALUE)) {
            return (root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThan(
                            root.get(BookSpecificationConstants.PRICE), bottomPrice
                    );
        }

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(
                        root.get(BookSpecificationConstants.PRICE), bottomPrice, upperPrice);
    }
}
