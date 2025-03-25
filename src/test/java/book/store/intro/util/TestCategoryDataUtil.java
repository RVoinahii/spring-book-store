package book.store.intro.util;

import static book.store.intro.util.TestBookDataUtil.DEFAULT_ID_SAMPLE;

import book.store.intro.dto.category.CategoryDto;
import book.store.intro.dto.category.CreateCategoryRequestDto;
import book.store.intro.model.Category;

public class TestCategoryDataUtil {
    public static final String CATEGORY_NAME = "CategoryOne";

    public static CreateCategoryRequestDto createCategoryRequestDtoSample() {
        return new CreateCategoryRequestDto(CATEGORY_NAME, null);
    }

    public static Category createDefaultCategorySample() {
        Category category = new Category();
        category.setId(DEFAULT_ID_SAMPLE);
        category.setName(CATEGORY_NAME);
        return category;
    }

    public static CategoryDto createCategoryDtoSampleFromEntity(Category category) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(categoryDto.getId());
        categoryDto.setName(category.getName());
        return categoryDto;
    }

    public static CategoryDto createDefaultCategoryDtoSample() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(DEFAULT_ID_SAMPLE);
        categoryDto.setName(CATEGORY_NAME);
        return categoryDto;
    }
}
