package book.store.intro.mapper;

import book.store.intro.config.MapperConfig;
import book.store.intro.dto.shopping.cart.ShoppingCartDto;
import book.store.intro.dto.shopping.cart.item.CartItemDto;
import book.store.intro.model.ShoppingCart;
import java.util.List;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class, uses = CartItemsMapper.class)
public interface ShoppingCartMapper {
    @Mapping(target = "userId", source = "user.id")
    ShoppingCartDto toDto(ShoppingCart shoppingCart);

    @AfterMapping
    default void setCartItems(@MappingTarget ShoppingCartDto shoppingCartDto,
                              ShoppingCart shoppingCart, CartItemsMapper cartItemsMapper) {
        List<CartItemDto> cartItemsDtoList = shoppingCart.getCartItems().stream()
                .map(cartItemsMapper::toDto)
                .toList();
        shoppingCartDto.setCartItems(cartItemsDtoList);
    }
}
