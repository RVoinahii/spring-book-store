package mate.academy.intro.service.category;

import java.util.List;
import mate.academy.intro.dto.book.BookWithoutCategoriesDto;
import mate.academy.intro.dto.category.CategoryDto;
import mate.academy.intro.dto.category.CreateCategoryRequestDto;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    List<CategoryDto> findAll(Pageable pageable);

    CategoryDto getById(Long id);

    List<BookWithoutCategoriesDto> getBooksByCategoryId(Pageable pageable, Long id);

    CategoryDto save(CreateCategoryRequestDto categoryDto);

    CategoryDto update(Long id, CreateCategoryRequestDto categoryDto);

    void deleteById(Long id);
}
