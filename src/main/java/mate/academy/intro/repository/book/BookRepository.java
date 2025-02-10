package mate.academy.intro.repository.book;

import mate.academy.intro.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book>,
        PagingAndSortingRepository<Book, Long> {
}
