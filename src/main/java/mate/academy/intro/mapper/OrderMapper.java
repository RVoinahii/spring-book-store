package mate.academy.intro.mapper;

import java.util.List;
import mate.academy.intro.config.MapperConfig;
import mate.academy.intro.dto.order.OrderDto;
import mate.academy.intro.dto.order.item.OrderItemDto;
import mate.academy.intro.model.Order;
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
