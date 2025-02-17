package mate.academy.intro.mapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import mate.academy.intro.config.MapperConfig;
import mate.academy.intro.dto.book.BookDto;
import mate.academy.intro.dto.book.BookWithoutCategoriesDto;
import mate.academy.intro.dto.book.CreateBookRequestDto;
import mate.academy.intro.model.Book;
import mate.academy.intro.model.Category;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    @Mapping(target = "categoryIds", ignore = true)
    BookDto toDto(Book book);

    @AfterMapping
    default void setCategoryIds(@MappingTarget BookDto bookDto, Book book) {
        List<Long> categoryIds = book.getCategories().stream()
                .map(Category::getId)
                .toList();
        bookDto.setCategoryIds(categoryIds);
    }

    BookWithoutCategoriesDto toBookWithoutCategoriesDto(Book book);

    @Mapping(target = "categories", ignore = true)
    Book toEntity(CreateBookRequestDto bookDto);

    @AfterMapping
    default void setCategories(@MappingTarget Book book, CreateBookRequestDto requestDto) {
        book.setCategories(mapCategoriesToEntity(requestDto));
    }

    @Mapping(target = "categories", ignore = true)
    void updateBookFromDto(CreateBookRequestDto updatedBook, @MappingTarget Book existingBook);

    @AfterMapping
    default void updateCategoriesAfterMapping(
            CreateBookRequestDto updatedBook, @MappingTarget Book existingBook) {
        existingBook.setCategories(mapCategoriesToEntity(updatedBook));
    }

    private Set<Category> mapCategoriesToEntity(CreateBookRequestDto requestDto) {
        return requestDto.getCategories().stream()
                .map(Category::new)
                .collect(Collectors.toSet());
    }
}
