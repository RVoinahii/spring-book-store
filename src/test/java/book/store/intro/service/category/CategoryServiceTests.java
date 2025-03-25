package book.store.intro.service.category;

import static book.store.intro.util.TestBookDataUtil.PAGE_NUMBER;
import static book.store.intro.util.TestBookDataUtil.PAGE_SIZE;
import static book.store.intro.util.TestBookDataUtil.createBookWithCustomCategorySample;
import static book.store.intro.util.TestBookDataUtil.createBookWithoutCategoriesDtoSampleFromEntity;
import static book.store.intro.util.TestCategoryDataUtil.createCategoryDtoSampleFromEntity;
import static book.store.intro.util.TestCategoryDataUtil.createCategoryRequestDtoSample;
import static book.store.intro.util.TestCategoryDataUtil.createDefaultCategorySample;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import book.store.intro.dto.book.BookWithoutCategoriesDto;
import book.store.intro.dto.category.CategoryDto;
import book.store.intro.dto.category.CreateCategoryRequestDto;
import book.store.intro.exceptions.EntityNotFoundException;
import book.store.intro.mapper.BookMapper;
import book.store.intro.mapper.CategoryMapper;
import book.store.intro.model.Book;
import book.store.intro.model.Category;
import book.store.intro.repository.book.BookRepository;
import book.store.intro.repository.category.CategoryRepository;
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

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTests {
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
        Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        Category category = createDefaultCategorySample();
        CategoryDto expectedCategoryDto = createCategoryDtoSampleFromEntity(category);

        List<Category> categories = List.of(category);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDto(category)).thenReturn(expectedCategoryDto);

        //When
        Page<CategoryDto> actualCategoryDtoPage = categoryService.getAll(pageable);

        //Then
        assertThat(actualCategoryDtoPage).hasSize(1);
        assertThat(actualCategoryDtoPage.getContent().getFirst()).isEqualTo(expectedCategoryDto);
        verify(categoryRepository).findAll(pageable);
        verify(categoryMapper).toDto(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("""
            getById():
             Should return correct CategoryDto when category exists
            """)
    void getById_WithValidBookId_ShouldReturnValidBookDto() {
        //Given
        Long categoryId = 1L;

        Category category = createDefaultCategorySample();
        CategoryDto expectedCategoryDto = createCategoryDtoSampleFromEntity(category);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(expectedCategoryDto);

        //When
        CategoryDto actualCategoryDtoById = categoryService.getById(categoryId);

        //Then
        assertThat(actualCategoryDtoById).isEqualTo(expectedCategoryDto);
        verify(categoryRepository).findById(categoryId);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("""
            geById():
             Should throw EntityNotFoundException when category doesn't exist
            """)
    void getById_WithInvalidBookId_ShouldThrowException() {
        //Given
        Long categoryId = 1L;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        //When
        Exception exception = assertThrows(
                EntityNotFoundException.class, () -> categoryService.getById(categoryId)
        );

        //Then
        String expected = "Can't find category by id: " + categoryId;
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(categoryRepository).findById(categoryId);
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
        Long categoryId = 1L;

        Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

        Category category = createDefaultCategorySample();

        Book book = createBookWithCustomCategorySample(category);
        BookWithoutCategoriesDto expectedBookDto =
                createBookWithoutCategoriesDtoSampleFromEntity(book);

        List<Book> books = List.of(book);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());

        when(bookRepository.findAllByCategoryId(pageable, categoryId)).thenReturn(bookPage);
        when(bookMapper.toBookWithoutCategoriesDto(book)).thenReturn(expectedBookDto);

        //When
        Page<BookWithoutCategoriesDto> actualBookDtoPage = categoryService.getBooksByCategoryId(
                pageable, categoryId);

        //Then
        assertThat(actualBookDtoPage).hasSize(1);
        assertThat(actualBookDtoPage.getContent().getFirst()).isEqualTo(expectedBookDto);
        verify(bookRepository).findAllByCategoryId(pageable, categoryId);
        verify(bookMapper).toBookWithoutCategoriesDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("""
            getBooksByCategoryId():
             Should throw EntityNotFoundException when category with given ID doesn't exist
            """)
    void getBooksByCategoryId_InvalidCategoryId_ShouldThrowException() {
        //Given
        Long categoryId = 1L;

        Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

        when(bookRepository.findAllByCategoryId(pageable, categoryId)).thenReturn(Page.empty());

        //When
        Exception exception = assertThrows(
                EntityNotFoundException.class, () -> categoryService
                        .getBooksByCategoryId(pageable, categoryId)
        );

        //Then
        String expected = "Can't find books for category id: " + categoryId;
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(bookRepository).findAllByCategoryId(pageable, categoryId);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("""
            create():
             Should return the correct CategoryDto when a category is successfully created
            """)
    void create_ValidCreateCategoryRequestDto_ReturnsBookDto() {
        //Given
        CreateCategoryRequestDto requestDto = createCategoryRequestDtoSample();
        Category category = createDefaultCategorySample();
        CategoryDto expectedCategoryDto = createCategoryDtoSampleFromEntity(category);

        when(categoryMapper.toEntity(requestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expectedCategoryDto);

        //When
        CategoryDto actualCategoryDto = categoryService.create(requestDto);

        //Then
        assertThat(actualCategoryDto).isEqualTo(expectedCategoryDto);
        verify(categoryRepository).save(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("""
            updateById():
             Should return correct CategoryDto when category is successfully updated
            """)
    void updateById_WithValidId_ShouldReturnValidDto() {
        //Given
        Long categoryId = 1L;

        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto(
                "New Name", "New Description");

        Category existingCategory = createDefaultCategorySample();

        Category updatedCategory = createDefaultCategorySample();
        updatedCategory.setName(requestDto.name());
        updatedCategory.setDescription(requestDto.description());

        CategoryDto expectedCategoryDto = createCategoryDtoSampleFromEntity(updatedCategory);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryMapper.toDto(categoryRepository.save(updatedCategory)))
                .thenReturn(expectedCategoryDto);

        // When
        CategoryDto actualCategoryDto = categoryService.updateById(categoryId, requestDto);

        // Then
        assertThat(actualCategoryDto).isEqualTo(expectedCategoryDto);
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).save(updatedCategory);
        verify(categoryMapper).toDto(categoryRepository.save(updatedCategory));
    }

    @Test
    @DisplayName("""
            updateById():
             Should throw EntityNotFoundException when the category doesn't exist during update
            """)
    void updateById_WithInvalidCategoryId_ShouldThrowException() {
        //Given
        Long categoryId = 1L;

        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto(
                "New Name", "New Description");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        //When
        Exception exception = assertThrows(
                EntityNotFoundException.class, () -> categoryService.updateById(
                        categoryId, requestDto)
        );

        //Then
        String expected = "Can't find category by id: " + categoryId;
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(categoryRepository).findById(categoryId);
        verifyNoMoreInteractions(categoryRepository);
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
        categoryService.deleteById(bookId);

        //Then
        verify(categoryRepository).deleteById(bookId);
        verifyNoMoreInteractions(categoryRepository);
    }
}
