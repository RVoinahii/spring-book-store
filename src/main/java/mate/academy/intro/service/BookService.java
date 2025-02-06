package mate.academy.intro.service;

import java.util.List;
import mate.academy.intro.dto.BookDto;
import mate.academy.intro.dto.BookSearchParameters;
import mate.academy.intro.dto.CreateBookRequestDto;
import org.springframework.data.domain.Sort;

public interface BookService {
    BookDto create(CreateBookRequestDto bookDto);

    List<BookDto> getAll(Integer pageNumber, Integer pageSize, Sort sort);

    BookDto getById(Long id);

    List<BookDto> search(BookSearchParameters params,
                         Integer pageNumber, Integer pageSize, Sort sort);

    BookDto updateById(Long id, CreateBookRequestDto bookDto);

    void deleteById(Long id);
}
