package mate.academy.intro.service;

import static mate.academy.intro.repository.book.BookSpecificationBuilder.AUTHOR;
import static mate.academy.intro.repository.book.BookSpecificationBuilder.TITLE;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.intro.dto.BookDto;
import mate.academy.intro.dto.BookSearchParameters;
import mate.academy.intro.dto.CreateBookRequestDto;
import mate.academy.intro.exceptions.EntityNotFoundException;
import mate.academy.intro.mapper.BookMapper;
import mate.academy.intro.model.Book;
import mate.academy.intro.repository.book.BookRepository;
import mate.academy.intro.repository.book.BookSpecificationBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder bookSpecificationBuilder;

    @Override
    public BookDto create(CreateBookRequestDto bookDto) {
        Book book = bookMapper.toModel(bookDto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public List<BookDto> getAll(Integer pageNumber, Integer pageSize, Sort sort) {
        return bookRepository.findAll(getValidatedPageable(pageNumber, pageSize, sort)).stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public BookDto getById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find book by id: " + id)
        );
        return bookMapper.toDto(book);
    }

    @Override
    public List<BookDto> search(BookSearchParameters searchParameters,
                                Integer pageNumber, Integer pageSize, Sort sort) {
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(searchParameters);
        return bookRepository.findAll(bookSpecification,
                        getValidatedPageable(pageNumber, pageSize, sort)).stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public BookDto updateById(Long id, CreateBookRequestDto updatedBookDataDto) {
        Book existingBook = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find book by id: " + id));
        bookMapper.updateBookFromDto(updatedBookDataDto, existingBook);
        return bookMapper.toDto(bookRepository.save(existingBook));
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    private Pageable getValidatedPageable(Integer pageNumber, Integer pageSize, Sort sort) {
        pageNumber = (pageNumber != null && pageNumber >= 0) ? pageNumber : 0;
        pageSize = (pageSize != null && pageSize >= 0) ? pageSize : 10;
        sort = (sort != null) ? sort : Sort.by(Sort.Order.asc(TITLE), Sort.Order.asc(AUTHOR));
        return PageRequest.of(pageNumber, pageSize, sort);
    }
}
