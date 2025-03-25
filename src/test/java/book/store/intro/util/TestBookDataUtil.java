package book.store.intro.util;

import static book.store.intro.util.TestCategoryDataUtil.CATEGORY_NAME;

import book.store.intro.dto.book.BookDto;
import book.store.intro.dto.book.BookWithoutCategoriesDto;
import book.store.intro.dto.book.CreateBookRequestDto;
import book.store.intro.model.Book;
import book.store.intro.model.Category;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public class TestBookDataUtil {
    public static final Long DEFAULT_ID_SAMPLE = 1L;
    public static final int PAGE_NUMBER = 0;
    public static final int PAGE_SIZE = 10;
    public static final String BOOK_TITLE = "BookOne";
    public static final String BOOK_AUTHOR = "AuthorOne";
    public static final String BOOK_ISBN = "978-3-16-148410-0";
    public static final BigDecimal BOOK_PRICE = BigDecimal.valueOf(39.99);

    public static CreateBookRequestDto createBookRequestDtoSample() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle(BOOK_TITLE);
        requestDto.setAuthor(BOOK_AUTHOR);
        requestDto.setIsbn(BOOK_ISBN);
        requestDto.setPrice(BOOK_PRICE);
        requestDto.setCategories(List.of(1L));
        return requestDto;
    }

    public static Book createDefaultBookSample() {
        Book book = new Book();
        book.setId(DEFAULT_ID_SAMPLE);
        book.setTitle(BOOK_TITLE);
        book.setAuthor(BOOK_AUTHOR);
        book.setIsbn(BOOK_ISBN);
        book.setPrice(BOOK_PRICE);

        Category category = new Category();
        category.setId(DEFAULT_ID_SAMPLE);
        category.setName(CATEGORY_NAME);

        book.setCategories(Set.of(category));
        return book;
    }

    public static Book createBookWithCustomCategorySample(Category category) {
        Book book = new Book();
        book.setId(DEFAULT_ID_SAMPLE);
        book.setTitle(BOOK_TITLE);
        book.setAuthor(BOOK_AUTHOR);
        book.setIsbn(BOOK_ISBN);
        book.setPrice(BOOK_PRICE);
        book.setCategories(Set.of(category));
        return book;
    }

    public static BookDto createBookDtoSampleFromEntity(Book book) {
        BookDto bookDto = new BookDto();
        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setPrice(book.getPrice());

        Set<Category> categories = book.getCategories();
        List<Long> categoriesIds = categories.stream()
                .map(Category::getId)
                .toList();

        bookDto.setCategoryIds(categoriesIds);
        return bookDto;
    }

    public static BookDto createDefaultBookDtoSample() {
        BookDto bookDto = new BookDto();
        bookDto.setTitle(BOOK_TITLE);
        bookDto.setAuthor(BOOK_AUTHOR);
        bookDto.setIsbn(BOOK_ISBN);
        bookDto.setPrice(BOOK_PRICE);
        bookDto.setCategoryIds(List.of(1L));
        return bookDto;
    }

    public static BookWithoutCategoriesDto createBookWithoutCategoriesDtoSampleFromEntity(
            Book book) {
        BookWithoutCategoriesDto bookDto = new BookWithoutCategoriesDto();
        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setPrice(book.getPrice());
        return bookDto;
    }

    public static BookWithoutCategoriesDto createDefaultBookWithoutCategoriesDtoSample() {
        BookWithoutCategoriesDto bookDto = new BookWithoutCategoriesDto();
        bookDto.setTitle(BOOK_TITLE);
        bookDto.setAuthor(BOOK_AUTHOR);
        bookDto.setIsbn(BOOK_ISBN);
        bookDto.setPrice(BOOK_PRICE);
        return bookDto;
    }
}
