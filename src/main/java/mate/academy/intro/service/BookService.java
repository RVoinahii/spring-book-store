package mate.academy.intro.service;

import java.util.List;
import mate.academy.intro.model.Book;

public interface BookService {
    Book save(Book book);

    List<Book> findAll();
}
