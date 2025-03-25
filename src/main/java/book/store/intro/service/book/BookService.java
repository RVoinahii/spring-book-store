package book.store.intro.service.book;

import book.store.intro.dto.book.BookDto;
import book.store.intro.dto.book.BookSearchParameters;
import book.store.intro.dto.book.CreateBookRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    Page<BookDto> getAll(Pageable pageable);

    BookDto getById(Long id);

    Page<BookDto> search(BookSearchParameters params, Pageable pageable);

    BookDto create(CreateBookRequestDto bookDto);

    BookDto updateById(Long id, CreateBookRequestDto bookDto);

    void deleteById(Long id);
}
