package book.store.intro.controller;

import static book.store.intro.repository.book.BookSpecificationBuilder.AUTHOR;
import static book.store.intro.repository.book.BookSpecificationBuilder.TITLE;

import book.store.intro.dto.book.BookWithoutCategoriesDto;
import book.store.intro.dto.category.CategoryDto;
import book.store.intro.dto.category.CreateCategoryRequestDto;
import book.store.intro.service.category.CategoryService;
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

@Tag(name = "Category management", description = "Endpoints for managing categories")
@SecurityRequirement(name = "BearerAuthentication")
@RequiredArgsConstructor
@RestController
@RequestMapping("/categories")
public class CategoryController {
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";

    private final CategoryService categoryService;

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping
    @Operation(
            summary = "Get all categories",
            description = "Get a paginated list of all available categories in the library "
                    + "(Required roles: USER, ADMIN)"
    )
    public Page<CategoryDto> getAllCategories(@ParameterObject @PageableDefault(
            sort = {NAME, DESCRIPTION}, direction = Sort.Direction.ASC
    ) Pageable pageable) {
        return categoryService.getAll(pageable);
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/{id}")
    @Operation(
            summary = "Get a category by ID",
            description = "Get category by the given ID (Required roles: USER, ADMIN)"
    )
    public CategoryDto getCategoryById(@PathVariable Long id) {
        return categoryService.getById(id);
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/{id}/books")
    @Operation(
            summary = "Get all books by category ID",
            description = "Get a paginated list of all available books in the library "
                    + "by category ID (Required roles: USER, ADMIN)"
    )
    public Page<BookWithoutCategoriesDto> getBooksByCategoryId(@ParameterObject @PageableDefault(
            sort = {TITLE, AUTHOR}, direction = Sort.Direction.ASC
    ) Pageable pageable, @PathVariable Long id) {
        return categoryService.getBooksByCategoryId(pageable, id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    @Operation(
            summary = "Create a new category",
            description = "Create a new category with the provided parameters "
                    + "(Required roles: ADMIN)"
    )
    public CategoryDto createCategory(@RequestBody @Valid CreateCategoryRequestDto categoryDto) {
        return categoryService.create(categoryDto);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    @Operation(
            summary = "Update category by ID",
            description = "Update category by the given ID with the provided parameters "
                    + "(Required roles: ADMIN)"
    )
    public CategoryDto updateCategoryById(@PathVariable Long id,
                                      @RequestBody @Valid CreateCategoryRequestDto categoryDto) {
        return categoryService.updateById(id, categoryDto);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a category by ID",
            description = "Mark a category as deleted by the given ID "
                    + "(Required roles: ADMIN)"
    )
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteById(id);
    }
}
