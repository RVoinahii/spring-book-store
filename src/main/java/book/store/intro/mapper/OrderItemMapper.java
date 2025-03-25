package book.store.intro.mapper;

import book.store.intro.config.MapperConfig;
import book.store.intro.dto.order.item.OrderItemDto;
import book.store.intro.model.CartItem;
import book.store.intro.model.Order;
import book.store.intro.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {
    @Mapping(target = "bookId", source = "book.id")
    OrderItemDto toDto(OrderItem orderItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "price", source = "cartItem.book.price")
    OrderItem cartItemToOrderItem(CartItem cartItem, Order order);
}
