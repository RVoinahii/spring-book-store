package book.store.intro.service.category;

import book.store.intro.dto.book.BookWithoutCategoriesDto;
import book.store.intro.dto.category.CategoryDto;
import book.store.intro.dto.category.CreateCategoryRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    Page<CategoryDto> getAll(Pageable pageable);

    CategoryDto getById(Long id);

    Page<BookWithoutCategoriesDto> getBooksByCategoryId(Pageable pageable, Long id);

    CategoryDto create(CreateCategoryRequestDto categoryDto);

    CategoryDto updateById(Long id, CreateCategoryRequestDto categoryDto);

    void deleteById(Long id);
}
