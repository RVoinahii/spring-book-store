package book.store.intro.service.book;

import static book.store.intro.util.TestBookDataUtil.PAGE_NUMBER;
import static book.store.intro.util.TestBookDataUtil.PAGE_SIZE;
import static book.store.intro.util.TestBookDataUtil.createBookDtoSampleFromEntity;
import static book.store.intro.util.TestBookDataUtil.createBookRequestDtoSample;
import static book.store.intro.util.TestBookDataUtil.createDefaultBookSample;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import book.store.intro.dto.book.BookDto;
import book.store.intro.dto.book.BookSearchParameters;
import book.store.intro.dto.book.CreateBookRequestDto;
import book.store.intro.exceptions.EntityNotFoundException;
import book.store.intro.mapper.BookMapper;
import book.store.intro.model.Book;
import book.store.intro.repository.book.BookRepository;
import book.store.intro.repository.book.BookSpecificationBuilder;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
public class BookServiceTests {
    @InjectMocks
    private BookServiceImpl bookService;
    
    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;

    @Test
    @DisplayName("""
            getAll():
             Should return correct Page<BookDto> when pageable is valid
            """)
    void getAll_ValidPageable_ReturnsAllBooks() {
        //Given
        Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        Book book = createDefaultBookSample();
        BookDto expectedBookDto = createBookDtoSampleFromEntity(book);

        List<Book> books = List.of(book);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(expectedBookDto);

        //When
        Page<BookDto> actualBookDtosPage = bookService.getAll(pageable);

        //Then
        assertThat(actualBookDtosPage).hasSize(1);
        assertThat(actualBookDtosPage.getContent().getFirst()).isEqualTo(expectedBookDto);
        verify(bookRepository).findAll(pageable);
        verify(bookMapper).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("""
            getById():
             Should return correct BookDto when book exists
            """)
    void getById_WithValidBookId_ShouldReturnValidBookDto() {
        //Given
        Long bookId = 1L;

        Book book = createDefaultBookSample();
        BookDto expectedBookDto = createBookDtoSampleFromEntity(book);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(expectedBookDto);

        //When
        BookDto actualBookDto = bookService.getById(bookId);

        //Then
        assertThat(actualBookDto).isEqualTo(expectedBookDto);
        verify(bookRepository).findById(bookId);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("""
            geById():
             Should throw EntityNotFoundException when book doesn't exist
            """)
    void getById_WithInvalidBookId_ShouldThrowException() {
        //Given
        Long bookId = 1L;

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        //When
        Exception exception = assertThrows(
                EntityNotFoundException.class, () -> bookService.getById(bookId)
        );

        //Then
        String expected = "Can't find book by id: " + bookId;
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(bookRepository).findById(bookId);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("""
            search():
             Should return Page of BookDto when search parameters are valid
            """)
    void search_WithValidParameters_ShouldReturnPageOfBookDtos() {
        //Given
        BookSearchParameters searchParameters = new BookSearchParameters(
                "Java", null, null, null, null
        );
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(searchParameters);
        Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

        Book bookSampleWithTitleJava = createDefaultBookSample();
        bookSampleWithTitleJava.setTitle("Java");

        BookDto expectedBookDto = createBookDtoSampleFromEntity(bookSampleWithTitleJava);

        List<Book> books = List.of(bookSampleWithTitleJava);
        Page<Book> bookPage = new PageImpl<>(books, pageable, 1);

        when(bookRepository.findAll(eq(bookSpecification), eq(pageable))).thenReturn(bookPage);
        when(bookMapper.toDto(bookSampleWithTitleJava)).thenReturn(expectedBookDto);

        //When
        Page<BookDto> actualBookDtosPage = bookService.search(searchParameters, pageable);

        //Then
        assertThat(actualBookDtosPage).hasSize(1);
        assertThat(actualBookDtosPage.getContent().getFirst()).isEqualTo(expectedBookDto);
        verify(bookRepository).findAll(bookSpecification, pageable);
        verify(bookMapper).toDto(bookSampleWithTitleJava);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("""
            create():
             Should return the correct BookDto when a book is successfully created
            """)
    void create_ValidCreateBookRequestDto_ReturnsBookDto() {
        //Given
        CreateBookRequestDto requestDto = createBookRequestDtoSample();
        Book book = createDefaultBookSample();
        BookDto expectedBookDto = createBookDtoSampleFromEntity(book);

        when(bookMapper.toEntity(requestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(expectedBookDto);

        //When
        BookDto actualBookDto = bookService.create(requestDto);

        //Then
        assertThat(actualBookDto).isEqualTo(expectedBookDto);
        verify(bookRepository).save(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("""
            updateById():
             Should return correct BookDto when book is successfully updated
            """)
    void updateById_WithValidId_ShouldReturnValidDto() {
        //Given
        CreateBookRequestDto requestDto = createBookRequestDtoSample();
        requestDto.setTitle("New Title");
        requestDto.setAuthor("New Author");

        Book existingBook = createDefaultBookSample();

        Book updatedBook = createDefaultBookSample();
        updatedBook.setTitle(requestDto.getTitle());
        updatedBook.setAuthor(requestDto.getAuthor());

        BookDto expectedBookDto = createBookDtoSampleFromEntity(updatedBook);

        Long bookId = 1L;

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(bookMapper.toDto(bookRepository.save(updatedBook))).thenReturn(expectedBookDto);

        // When
        BookDto actualBookDto = bookService.updateById(bookId, requestDto);

        // Then
        assertThat(actualBookDto).isEqualTo(expectedBookDto);
        verify(bookRepository).findById(bookId);
        verify(bookRepository).save(updatedBook);
        verify(bookMapper).toDto(bookRepository.save(updatedBook));
    }

    @Test
    @DisplayName("""
            updateById():
             Should throw EntityNotFoundException when the book doesn't exist during update
            """)
    void updateById_WithInvalidBookId_ShouldThrowException() {
        //Given
        Long bookId = 1L;

        CreateBookRequestDto requestDto = createBookRequestDtoSample();
        requestDto.setTitle("New Title");
        requestDto.setAuthor("New Author");

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        //When
        Exception exception = assertThrows(
                EntityNotFoundException.class, () -> bookService.updateById(bookId, requestDto)
        );

        //Then
        String expected = "Can't find book by id: " + bookId;
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(bookRepository).findById(bookId);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("""
            deleteById():
             Should delete book by ID when a valid ID is provided
            """)
    void deleteById_WithValidId_ShouldInvokeRepositoryOnce() {
        //Given
        Long bookId = 1L;

        //When
        bookService.deleteById(bookId);

        //Then
        verify(bookRepository).deleteById(bookId);
        verifyNoMoreInteractions(bookRepository);
    }
}
