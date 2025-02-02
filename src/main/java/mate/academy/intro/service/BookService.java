package mate.academy.intro.service;

import java.util.List;
import mate.academy.intro.dto.BookDto;
import mate.academy.intro.dto.BookSearchParameters;
import mate.academy.intro.dto.CreateBookRequestDto;

public interface BookService {
    BookDto create(CreateBookRequestDto bookDto);

    List<BookDto> getAll();

    BookDto getById(Long id);

    List<BookDto> search(BookSearchParameters params);

    BookDto updateById(Long id, CreateBookRequestDto bookDto);

    void deleteById(Long id);
}
