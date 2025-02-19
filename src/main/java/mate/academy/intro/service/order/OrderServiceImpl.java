package mate.academy.intro.service.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import mate.academy.intro.dto.order.CreateOrderRequestDto;
import mate.academy.intro.dto.order.OrderDto;
import mate.academy.intro.dto.order.UpdateOrderStatusRequestDto;
import mate.academy.intro.dto.order.item.OrderItemDto;
import mate.academy.intro.exceptions.EntityNotFoundException;
import mate.academy.intro.mapper.OrderItemMapper;
import mate.academy.intro.mapper.OrderMapper;
import mate.academy.intro.model.CartItem;
import mate.academy.intro.model.Order;
import mate.academy.intro.model.Order.Status;
import mate.academy.intro.model.OrderItem;
import mate.academy.intro.model.ShoppingCart;
import mate.academy.intro.model.User;
import mate.academy.intro.repository.order.OrderRepository;
import mate.academy.intro.service.shopping.cart.ShoppingCartService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final ShoppingCartService shoppingCartService;
    private final OrderItemMapper orderItemMapper;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderDto placeOrder(CreateOrderRequestDto requestDto, User user) {
        ShoppingCart shoppingCart = shoppingCartService.findShoppingCartByUserId(user.getId());
        if (shoppingCart.getCartItems() == null || shoppingCart.getCartItems().isEmpty()) {
            throw new IllegalStateException("Shopping cart is empty");
        }
        Set<CartItem> cartItems = new LinkedHashSet<>(shoppingCart.getCartItems());
        Order order = createOrderWithoutItems(requestDto, user);
        cartItems.forEach(cartItem -> {
            OrderItem orderItem = orderItemMapper.cartItemToOrderItem(cartItem, order);
            order.getOrderItems().add(orderItem);
            order.setTotal(order.getTotal().add(
                    orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()))));
        });
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public Page<OrderDto> getAllOrders(Pageable pageable, Long userId) {
        return orderRepository.findAllByUserId(pageable, userId)
                .map(orderMapper::toDto);
    }

    @Override
    public List<OrderItemDto> getOrderItems(Long orderId) {
        Order order = getOrderByIdWithOrderItems(orderId);
        return order.getOrderItems().stream()
                .map(orderItemMapper::toDto)
                .toList();
    }

    @Override
    public OrderItemDto getOrderItemInfo(Long orderId, Long itemId) {
        Order order = getOrderByIdWithOrderItems(orderId);
        OrderItem existingOrderItem = order.getOrderItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Can't find order item with id: " + itemId)
                );
        return orderItemMapper.toDto(existingOrderItem);
    }

    @Override
    public OrderDto updateOrderStatus(UpdateOrderStatusRequestDto requestDto, Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new EntityNotFoundException("Can't find order with id: " + orderId));
        order.setStatus(Status.valueOf(requestDto.status()));
        return orderMapper.toDto(orderRepository.save(order));
    }

    private Order createOrderWithoutItems(CreateOrderRequestDto requestDto, User user) {
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Status.PENDING);
        order.setTotal(BigDecimal.ZERO);
        order.setShippingAddress(requestDto.shippingAddress());
        order.setOrderItems(new HashSet<>());
        return order;
    }

    private Order getOrderByIdWithOrderItems(Long orderId) {
        return orderRepository.findByIdWithOrderItems(orderId).orElseThrow(
                () -> new EntityNotFoundException("Can't find order with id: " + orderId));
    }
}
