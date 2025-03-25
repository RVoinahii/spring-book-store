package book.store.intro.controller;

import static book.store.intro.repository.book.BookSpecificationBuilder.AUTHOR;
import static book.store.intro.repository.book.BookSpecificationBuilder.TITLE;

import book.store.intro.dto.book.BookDto;
import book.store.intro.dto.book.BookSearchParameters;
import book.store.intro.dto.book.CreateBookRequestDto;
import book.store.intro.service.book.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Book management", description = "Endpoints for managing books")
@SecurityRequirement(name = "BearerAuthentication")
@RequiredArgsConstructor
@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping
    @Operation(
            summary = "Get all books",
            description = "Get a paginated list of all available books in the library "
                    + "(Required roles: USER, ADMIN)"
    )
    public Page<BookDto> getAllBooks(@ParameterObject @PageableDefault(sort = {TITLE, AUTHOR},
            direction = Sort.Direction.ASC) Pageable pageable) {
        return bookService.getAll(pageable);
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/{id}")
    @Operation(
            summary = "Get a book by ID",
            description = "Get book by the given ID (Required roles: USER, ADMIN)"
    )
    public BookDto getBookById(@PathVariable Long id) {
        return bookService.getById(id);
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/search")
    @Operation(
            summary = "Get all books by parameters",
            description = "Search for books by the given parameters (title, author, etc.) "
                    + "(Required roles: USER, ADMIN)"
    )
    public Page<BookDto> search(BookSearchParameters searchParameters,
                                @ParameterObject @PageableDefault(sort = {TITLE, AUTHOR},
                                        direction = Sort.Direction.ASC) Pageable pageable) {
        return bookService.search(searchParameters, pageable);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    @Operation(
            summary = "Create a new book",
            description = "Create a new book with the provided parameters (Required roles: ADMIN)"
    )
    public BookDto createBook(@RequestBody @Valid CreateBookRequestDto bookDto) {
        return bookService.create(bookDto);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    @Operation(
            summary = "Update book by ID",
            description = "Update book by the given ID with the provided parameters "
                    + "(Required roles: ADMIN)"
    )
    public BookDto updateBook(@PathVariable Long id,
                              @RequestBody @Valid CreateBookRequestDto bookDto) {
        return bookService.updateById(id, bookDto);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a book by ID",
            description = "Mark a book as deleted by the given ID "
                    + "(Required roles: ADMIN)"
    )
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteById(id);
    }
}
