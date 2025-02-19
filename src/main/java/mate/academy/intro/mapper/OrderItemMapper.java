package mate.academy.intro.mapper;

import mate.academy.intro.config.MapperConfig;
import mate.academy.intro.dto.order.item.OrderItemDto;
import mate.academy.intro.model.CartItem;
import mate.academy.intro.model.Order;
import mate.academy.intro.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {
    @Mapping(target = "bookId", source = "book.id")
    OrderItemDto toDto(OrderItem orderItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", source = "order")
    @Mapping(target = "price", source = "cartItem.book.price")
    OrderItem cartItemToOrderItem(CartItem cartItem, Order order);
}
