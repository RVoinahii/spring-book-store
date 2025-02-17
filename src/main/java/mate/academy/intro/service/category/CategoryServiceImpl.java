package mate.academy.intro.service.category;

import lombok.RequiredArgsConstructor;
import mate.academy.intro.dto.book.BookWithoutCategoriesDto;
import mate.academy.intro.dto.category.CategoryDto;
import mate.academy.intro.dto.category.CreateCategoryRequestDto;
import mate.academy.intro.exceptions.EntityNotFoundException;
import mate.academy.intro.mapper.BookMapper;
import mate.academy.intro.mapper.CategoryMapper;
import mate.academy.intro.model.Category;
import mate.academy.intro.repository.book.BookRepository;
import mate.academy.intro.repository.category.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public Page<CategoryDto> findAll(Pageable pageable) {
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
        return bookRepository.findAllByCategoryId(pageable, id)
                .map(bookMapper::toBookWithoutCategoriesDto);
    }

    @Override
    public CategoryDto save(CreateCategoryRequestDto categoryDto) {
        Category category = categoryMapper.toEntity(categoryDto);
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public CategoryDto update(Long id, CreateCategoryRequestDto updatedCategoryDataDto) {
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
