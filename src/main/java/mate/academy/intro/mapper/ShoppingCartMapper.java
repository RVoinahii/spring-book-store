package mate.academy.intro.mapper;

import java.util.List;
import mate.academy.intro.config.MapperConfig;
import mate.academy.intro.dto.cart.item.CartItemDto;
import mate.academy.intro.dto.shopping.cart.ShoppingCartDto;
import mate.academy.intro.model.ShoppingCart;
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
