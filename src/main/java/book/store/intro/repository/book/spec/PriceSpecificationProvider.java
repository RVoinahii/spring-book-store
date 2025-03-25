package book.store.intro.repository.book.spec;

import static book.store.intro.repository.book.BookSpecificationBuilder.DELIMITER;
import static book.store.intro.repository.book.BookSpecificationBuilder.NO_VALUE;
import static book.store.intro.repository.book.BookSpecificationBuilder.PRICE;

import book.store.intro.model.Book;
import book.store.intro.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class PriceSpecificationProvider implements SpecificationProvider<Book> {
    private static final int BOTTOM_PRICE = 0;
    private static final int UPPER_PRICE = 1;

    @Override
    public String getKey() {
        return PRICE;
    }

    @Override
    public Specification<Book> getSpecification(String params) {
        String[] paramsSplit = params.split(DELIMITER);
        String bottomPrice = paramsSplit[BOTTOM_PRICE];
        String upperPrice = paramsSplit[UPPER_PRICE];

        if (bottomPrice.equals(NO_VALUE)) {
            return (root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThan(root.get(PRICE), upperPrice);
        }

        if (upperPrice.equals(NO_VALUE)) {
            return (root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThan(root.get(PRICE), bottomPrice);
        }

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get(PRICE), bottomPrice, upperPrice);
    }
}
