package book.store.intro.mapper;

import book.store.intro.config.MapperConfig;
import book.store.intro.dto.shopping.cart.item.CartItemDto;
import book.store.intro.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface CartItemsMapper {
    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "bookTitle", source = "book.title")
    CartItemDto toDto(CartItem cartItem);
}
