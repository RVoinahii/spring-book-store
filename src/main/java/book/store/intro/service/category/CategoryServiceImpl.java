package book.store.intro.service.category;

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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public Page<CategoryDto> getAll(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(categoryMapper::toDto);
    }

    @Override
    public CategoryDto getById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find category by id: " + id)
        );
        return categoryMapper.toDto(category);
    }

    @Override
    public Page<BookWithoutCategoriesDto> getBooksByCategoryId(Pageable pageable, Long id) {
        Page<Book> books = bookRepository.findAllByCategoryId(pageable, id);

        if (books.isEmpty()) {
            throw new EntityNotFoundException("Can't find books for category id: " + id);
        }
        return books.map(bookMapper::toBookWithoutCategoriesDto);
    }

    @Override
    public CategoryDto create(CreateCategoryRequestDto categoryDto) {
        Category category = categoryMapper.toEntity(categoryDto);
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public CategoryDto updateById(Long id, CreateCategoryRequestDto updatedCategoryDataDto) {
        Category existingCategory = categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find category by id: " + id));
        categoryMapper.updateCategoryFromDto(updatedCategoryDataDto, existingCategory);
        return categoryMapper.toDto(categoryRepository.save(existingCategory));
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
}
