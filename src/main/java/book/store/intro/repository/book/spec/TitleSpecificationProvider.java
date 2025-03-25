package book.store.intro.repository.book.spec;

import static book.store.intro.repository.book.BookSpecificationBuilder.TITLE;

import book.store.intro.model.Book;
import book.store.intro.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TitleSpecificationProvider implements SpecificationProvider<Book> {

    @Override
    public String getKey() {
        return TITLE;
    }

    @Override
    public Specification<Book> getSpecification(String params) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get(TITLE)), "%" + params.toLowerCase() + "%"
                );
    }
}
