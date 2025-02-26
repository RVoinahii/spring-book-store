package mate.academy.intro.service.category;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import mate.academy.intro.dto.book.BookWithoutCategoriesDto;
import mate.academy.intro.dto.category.CategoryDto;
import mate.academy.intro.dto.category.CreateCategoryRequestDto;
import mate.academy.intro.exceptions.EntityNotFoundException;
import mate.academy.intro.mapper.BookMapper;
import mate.academy.intro.mapper.CategoryMapper;
import mate.academy.intro.model.Book;
import mate.academy.intro.model.Category;
import mate.academy.intro.repository.book.BookRepository;
import mate.academy.intro.repository.category.CategoryRepository;
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

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTests {
    private static final Long BOOK_ID = 1L;
    private static final Long CATEGORY_ID = 1L;
    private static final String BOOK_TITLE = "BookOne";
    private static final String CATEGORY_TITLE = "CategoryOne";
    private static final String BOOK_AUTHOR = "AuthorOne";
    private static final String BOOK_ISBN = "978-3-16-148410-0";
    private static final BigDecimal BOOK_PRICE = BigDecimal.valueOf(39.99);

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private BookMapper bookMapper;

    @Test
    @DisplayName("""
            getAll():
             Should return correct Page<CategoryDto> when pageable is valid
            """)
    void getAll_ValidPageable_ReturnsAllBooks() {
        //Given
        Pageable pageable = PageRequest.of(0, 10);
        Category category = createCategorySample(CATEGORY_ID);
        CategoryDto expectedCategoryDto = createCategoryDtoSample(category);

        List<Category> categories = List.of(category);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDto(category)).thenReturn(expectedCategoryDto);

        //When
        Page<CategoryDto> actualCategoryDtoPage = categoryService.getAll(pageable);

        //Then
        assertThat(actualCategoryDtoPage).hasSize(1);
        assertThat(actualCategoryDtoPage.getContent().getFirst()).isEqualTo(expectedCategoryDto);
        verify(categoryRepository, times(1)).findAll(pageable);
        verify(categoryMapper, times(1)).toDto(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("""
            getById():
             Should return correct CategoryDto when category exists
            """)
    void getById_WithValidBookId_ShouldReturnValidBookDto() {
        //Given
        Category category = createCategorySample(CATEGORY_ID);
        CategoryDto expectedCategoryDto = createCategoryDtoSample(category);

        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(expectedCategoryDto);

        //When
        CategoryDto actualCategoryDtoById = categoryService.getById(CATEGORY_ID);

        //Then
        assertThat(actualCategoryDtoById).isEqualTo(expectedCategoryDto);
        verify(categoryRepository, times(1)).findById(CATEGORY_ID);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("""
            geById():
             Should throw EntityNotFoundException when category doesn't exist
            """)
    void getById_WithInvalidBookId_ShouldThrowException() {
        //Given
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.empty());

        //When
        Exception exception = assertThrows(
                EntityNotFoundException.class, () -> categoryService.getById(CATEGORY_ID)
        );

        //Then
        String expected = "Can't find category by id: " + CATEGORY_ID;
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(categoryRepository, times(1)).findById(CATEGORY_ID);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("""
            getBooksByCategoryId():
             Should return correct Page<BookWithoutCategoriesDto>
              when pageable and category ID is valid
            """)
    void getBooksByCategoryId_WithValidCategoryId_ShouldReturnValidBooks() {
        //Given
        Pageable pageable = PageRequest.of(0, 10);

        Category category = createCategorySample(CATEGORY_ID);

        Book book = createBookSample(BOOK_ID, category);
        BookWithoutCategoriesDto expectedBookDto = createBookWithoutCategoriesDtoSample(book);

        List<Book> books = List.of(book);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());

        when(bookRepository.findAllByCategoryId(pageable, CATEGORY_ID)).thenReturn(bookPage);
        when(bookMapper.toBookWithoutCategoriesDto(book)).thenReturn(expectedBookDto);

        //When
        Page<BookWithoutCategoriesDto> actualBookDtoPage = categoryService.getBooksByCategoryId(
                pageable, CATEGORY_ID);

        //Then
        assertThat(actualBookDtoPage).hasSize(1);
        assertThat(actualBookDtoPage.getContent().getFirst()).isEqualTo(expectedBookDto);
        verify(bookRepository, times(1)).findAllByCategoryId(pageable, CATEGORY_ID);
        verify(bookMapper, times(1)).toBookWithoutCategoriesDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("""
            create():
             Should return the correct CategoryDto when a category is successfully created
            """)
    void create_ValidCreateCategoryRequestDto_ReturnsBookDto() {
        //Given
        CreateCategoryRequestDto requestDto = createCategoryRequestDtoSample();
        Category category = createCategorySample(CATEGORY_ID);
        CategoryDto expectedCategoryDto = createCategoryDtoSample(category);

        when(categoryMapper.toEntity(requestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expectedCategoryDto);

        //When
        CategoryDto actualCategoryDto = categoryService.create(requestDto);

        //Then
        assertThat(actualCategoryDto).isEqualTo(expectedCategoryDto);
        verify(categoryRepository, times(1)).save(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("""
            updateById():
             Should return correct CategoryDto when category is successfully updated
            """)
    void updateById_WithValidId_ShouldReturnValidDto() {
        //Given
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto(
                "New Name", "New Description");

        Category existingCategory = createCategorySample(CATEGORY_ID);

        Category updatedCategory = createCategorySample(CATEGORY_ID);
        updatedCategory.setName(requestDto.name());
        updatedCategory.setDescription(requestDto.description());

        CategoryDto expectedCategoryDto = createCategoryDtoSample(updatedCategory);

        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(existingCategory));
        when(categoryMapper.toDto(categoryRepository.save(updatedCategory)))
                .thenReturn(expectedCategoryDto);

        // When
        CategoryDto actualCategoryDto = categoryService.updateById(CATEGORY_ID, requestDto);

        // Then
        assertThat(actualCategoryDto).isEqualTo(expectedCategoryDto);
        verify(categoryRepository, times(1)).findById(CATEGORY_ID);
        verify(categoryRepository, times(1)).save(updatedCategory);
        verify(categoryMapper, times(1)).toDto(categoryRepository.save(updatedCategory));
    }

    @Test
    @DisplayName("""
            updateById():
             Should throw EntityNotFoundException when the category doesn't exist during update
            """)
    void updateById_WithInvalidCategoryId_ShouldThrowException() {
        //Given
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto(
                "New Name", "New Description");

        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.empty());

        //When
        Exception exception = assertThrows(
                EntityNotFoundException.class, () -> categoryService.updateById(
                        CATEGORY_ID, requestDto)
        );

        //Then
        String expected = "Can't find category by id: " + CATEGORY_ID;
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(categoryRepository, times(1)).findById(CATEGORY_ID);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("""
            deleteById():
             Should delete book by ID when a valid ID is provided
            """)
    void deleteById_WithValidId_ShouldInvokeRepositoryOnce() {
        //Given
        Long bookId = CATEGORY_ID;

        //When
        categoryService.deleteById(bookId);

        //Then
        verify(categoryRepository, times(1)).deleteById(bookId);
        verifyNoMoreInteractions(categoryRepository);
    }

    private CreateCategoryRequestDto createCategoryRequestDtoSample() {
        return new CreateCategoryRequestDto("CategoryOne", null);
    }

    private Category createCategorySample(Long id) {
        Category category = new Category();
        category.setId(id);
        category.setName(CATEGORY_TITLE);
        return category;
    }

    private CategoryDto createCategoryDtoSample(Category category) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(categoryDto.getId());
        categoryDto.setName(category.getName());
        return categoryDto;
    }

    private Book createBookSample(Long id, Category category) {
        Book book = new Book();
        book.setId(id);
        book.setTitle(BOOK_TITLE);
        book.setAuthor(BOOK_AUTHOR);
        book.setIsbn(BOOK_ISBN);
        book.setPrice(BOOK_PRICE);
        book.setCategories(Set.of(category));
        return book;
    }

    private BookWithoutCategoriesDto createBookWithoutCategoriesDtoSample(Book book) {
        BookWithoutCategoriesDto bookDto = new BookWithoutCategoriesDto();
        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setPrice(book.getPrice());
        return bookDto;
    }
}
