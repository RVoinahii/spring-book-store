package mate.academy.intro.service.order;

import java.util.List;
import mate.academy.intro.dto.order.CreateOrderRequestDto;
import mate.academy.intro.dto.order.OrderDto;
import mate.academy.intro.dto.order.UpdateOrderStatusRequestDto;
import mate.academy.intro.dto.order.item.OrderItemDto;
import mate.academy.intro.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderDto placeOrder(CreateOrderRequestDto requestDto, User user);

    Page<OrderDto> getAllOrders(Pageable pageable, Long userId);

    List<OrderItemDto> getOrderItems(Long orderId);

    OrderItemDto getOrderItemInfo(Long orderId, Long itemId);

    OrderDto updateOrderStatus(UpdateOrderStatusRequestDto requestDto, Long orderId);
}
