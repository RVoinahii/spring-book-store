package book.store.intro.mapper;

import book.store.intro.config.MapperConfig;
import book.store.intro.dto.order.OrderDto;
import book.store.intro.dto.order.item.OrderItemDto;
import book.store.intro.model.Order;
import java.util.List;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class, uses = OrderItemMapper.class)
public interface OrderMapper {
    @Mapping(target = "userId", source = "user.id")
    OrderDto toDto(Order order);

    @AfterMapping
    default void setOrderItems(
            @MappingTarget OrderDto orderDto, Order order, OrderItemMapper orderItemMapper) {
        List<OrderItemDto> orderItemDtoList = order.getOrderItems().stream()
                .map(orderItemMapper::toDto)
                .toList();
        orderDto.setOrderItems(orderItemDtoList);
    }
}
