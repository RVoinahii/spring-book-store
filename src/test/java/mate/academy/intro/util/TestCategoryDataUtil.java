package mate.academy.intro.util;

import static mate.academy.intro.util.TestBookDataUtil.DEFAULT_ID_SAMPLE;

import mate.academy.intro.dto.category.CategoryDto;
import mate.academy.intro.dto.category.CreateCategoryRequestDto;
import mate.academy.intro.model.Category;

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
