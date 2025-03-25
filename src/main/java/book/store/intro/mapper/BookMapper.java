package book.store.intro.mapper;

import book.store.intro.config.MapperConfig;
import book.store.intro.dto.book.BookDto;
import book.store.intro.dto.book.BookWithoutCategoriesDto;
import book.store.intro.dto.book.CreateBookRequestDto;
import book.store.intro.model.Book;
import book.store.intro.model.Category;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
