package mate.academy.intro.service.book;

import mate.academy.intro.dto.book.BookDto;
import mate.academy.intro.dto.book.BookSearchParameters;
import mate.academy.intro.dto.book.CreateBookRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto create(CreateBookRequestDto bookDto);

    Page<BookDto> getAll(Pageable pageable);

    BookDto getById(Long id);

    Page<BookDto> search(BookSearchParameters params, Pageable pageable);

    BookDto updateById(Long id, CreateBookRequestDto bookDto);

    void deleteById(Long id);
}
