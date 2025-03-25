package book.store.intro.service.order;

import book.store.intro.dto.order.CreateOrderRequestDto;
import book.store.intro.dto.order.OrderDto;
import book.store.intro.dto.order.UpdateOrderStatusRequestDto;
import book.store.intro.dto.order.item.OrderItemDto;
import book.store.intro.model.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderDto placeOrder(CreateOrderRequestDto requestDto, User user);

    Page<OrderDto> getAllOrders(Pageable pageable, Long userId);

    List<OrderItemDto> getOrderItems(Long orderId);

    OrderItemDto getOrderItemInfo(Long orderId, Long itemId);

    OrderDto updateOrderStatus(UpdateOrderStatusRequestDto requestDto, Long orderId);
}
