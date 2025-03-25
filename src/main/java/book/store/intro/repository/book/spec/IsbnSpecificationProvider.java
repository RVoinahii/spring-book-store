package book.store.intro.repository.book.spec;

import static book.store.intro.repository.book.BookSpecificationBuilder.ISBN;

import book.store.intro.model.Book;
import book.store.intro.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class IsbnSpecificationProvider implements SpecificationProvider<Book> {

    @Override
    public String getKey() {
        return ISBN;
    }

    @Override
    public Specification<Book> getSpecification(String params) {
        return (root, query, criteriaBuilder) ->
                root.get(ISBN).in(params);
    }
}
