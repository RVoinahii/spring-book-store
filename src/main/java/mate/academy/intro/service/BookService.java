package mate.academy.intro.service;

import java.util.List;
import mate.academy.intro.dto.BookDto;
import mate.academy.intro.dto.CreateBookRequestDto;
import mate.academy.intro.dto.UpdateBookRequestDto;

public interface BookService {
    BookDto create(CreateBookRequestDto bookDto);

    List<BookDto> getAll();

    BookDto getById(Long id);

    BookDto updateById(Long id, UpdateBookRequestDto bookDto);

    void deleteById(Long id);
}
