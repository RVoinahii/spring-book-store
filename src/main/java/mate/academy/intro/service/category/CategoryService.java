package mate.academy.intro.service.category;

import mate.academy.intro.dto.book.BookWithoutCategoriesDto;
import mate.academy.intro.dto.category.CategoryDto;
import mate.academy.intro.dto.category.CreateCategoryRequestDto;
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
