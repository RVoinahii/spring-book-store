package book.store.intro.mapper;

import book.store.intro.config.MapperConfig;
import book.store.intro.dto.category.CategoryDto;
import book.store.intro.dto.category.CreateCategoryRequestDto;
import book.store.intro.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toEntity(CreateCategoryRequestDto categoryRequestDto);

    void updateCategoryFromDto(CreateCategoryRequestDto category, @MappingTarget Category entity);
}
