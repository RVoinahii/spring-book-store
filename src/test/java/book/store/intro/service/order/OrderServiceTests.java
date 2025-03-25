package book.store.intro.service.order;

import static book.store.intro.util.TestBookDataUtil.PAGE_NUMBER;
import static book.store.intro.util.TestBookDataUtil.PAGE_SIZE;
import static book.store.intro.util.TestBookDataUtil.createDefaultBookSample;
import static book.store.intro.util.TestOrderDataUtil.createEmptyOrderDtoSampleFromEntity;
import static book.store.intro.util.TestOrderDataUtil.createEmptyOrderSample;
import static book.store.intro.util.TestOrderDataUtil.createOrderItemDtoSampleFromEntity;
import static book.store.intro.util.TestOrderDataUtil.createOrderItemSample;
import static book.store.intro.util.TestShoppingCartDataUtil.createCartItemSample;
import static book.store.intro.util.TestShoppingCartDataUtil.createEmptyShoppingCartSample;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import book.store.intro.dto.order.CreateOrderRequestDto;
import book.store.intro.dto.order.OrderDto;
import book.store.intro.dto.order.UpdateOrderStatusRequestDto;
import book.store.intro.dto.order.item.OrderItemDto;
import book.store.intro.exceptions.EntityNotFoundException;
import book.store.intro.exceptions.OrderProcessingException;
import book.store.intro.mapper.OrderItemMapper;
import book.store.intro.mapper.OrderMapper;
import book.store.intro.model.Book;
import book.store.intro.model.CartItem;
import book.store.intro.model.Order;
import book.store.intro.model.OrderItem;
import book.store.intro.model.ShoppingCart;
import book.store.intro.model.User;
import book.store.intro.repository.order.OrderRepository;
import book.store.intro.service.shopping.cart.ShoppingCartServiceImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTests {
    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private ShoppingCartServiceImpl shoppingCartService;

    @Mock
    private OrderItemMapper orderItemMapper;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Test
    @DisplayName("""
            placeOrder():
             Should return the correct OrderDto when a valid order request is placed
            """)
    void placeOrder_ValidRequestDtoAndUser_ReturnOrderDto() {
        //Given
        String orderShippingAddress = "Address from request";

        User user = new User();
        user.setId(1L);

        ShoppingCart shoppingCart = createEmptyShoppingCartSample();
        shoppingCart.setUser(user);

        Book book = createDefaultBookSample();
        CartItem cartItem = createCartItemSample(shoppingCart, book);
        CreateOrderRequestDto requestDto = new CreateOrderRequestDto(orderShippingAddress);
        Order order = createEmptyOrderSample();
        order.setUser(user);
        order.setShippingAddress(requestDto.shippingAddress());
        OrderItem orderItem = createOrderItemSample(order, book);
        OrderItemDto orderItemDto = createOrderItemDtoSampleFromEntity(orderItem);
        OrderDto expectedOrderDto = createEmptyOrderDtoSampleFromEntity(order);
        expectedOrderDto.setOrderItems(new ArrayList<>(List.of(orderItemDto)));

        when(shoppingCartService.findShoppingCartByUserId(user.getId())).thenReturn(shoppingCart);
        when(orderItemMapper.cartItemToOrderItem(
                any(CartItem.class), any(Order.class))).thenReturn(orderItem);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(expectedOrderDto);

        //When
        OrderDto actualOrderDto = orderService.placeOrder(requestDto, user);

        //Then
        assertEquals(actualOrderDto, expectedOrderDto);
        verify(shoppingCartService).findShoppingCartByUserId(user.getId());
        verify(orderItemMapper).cartItemToOrderItem(any(CartItem.class), any(Order.class));
        verify(orderRepository).save(any(Order.class));
        verify(orderMapper).toDto(order);
        verifyNoMoreInteractions(shoppingCartService, orderItemMapper,
                orderRepository, orderMapper);
    }

    @Test
    @DisplayName("""
            placeOrder():
             Should throw an OrderProcessingException when the cart items are null
            """)
    void placeOrder_CartItemsIsNull_ShouldThrowException() {
        //Given
        User user = new User();
        user.setId(1L);

        ShoppingCart shoppingCart = createEmptyShoppingCartSample();
        shoppingCart.setUser(user);
        shoppingCart.setCartItems(null);

        String orderShippingAddress = "Address from request";
        CreateOrderRequestDto requestDto = new CreateOrderRequestDto(orderShippingAddress);

        when(shoppingCartService.findShoppingCartByUserId(user.getId())).thenReturn(shoppingCart);

        //When
        Exception exception = assertThrows(
                OrderProcessingException.class, () -> orderService.placeOrder(requestDto, user)
        );

        //Then
        String expected = "Shopping cart is empty";
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(shoppingCartService).findShoppingCartByUserId(user.getId());
        verifyNoMoreInteractions(shoppingCartService);
    }

    @Test
    @DisplayName("""
            placeOrder():
             Should throw an OrderProcessingException when the cart is empty
            """)
    void placeOrder_CartItemsIsEmpty_ShouldThrowException() {
        //Given
        User user = new User();
        user.setId(1L);

        ShoppingCart shoppingCart = createEmptyShoppingCartSample();
        shoppingCart.setUser(user);

        String orderShippingAddress = "Address from request";
        CreateOrderRequestDto requestDto = new CreateOrderRequestDto(orderShippingAddress);

        when(shoppingCartService.findShoppingCartByUserId(user.getId())).thenReturn(shoppingCart);

        //When
        Exception exception = assertThrows(
                OrderProcessingException.class, () -> orderService.placeOrder(requestDto, user)
        );

        //Then
        String expected = "Shopping cart is empty";
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(shoppingCartService).findShoppingCartByUserId(user.getId());
        verifyNoMoreInteractions(shoppingCartService);
    }

    @Test
    @DisplayName("""
            getAll():
             Should return a paged list of all orders for the given userId and pageable
            """)
    void getAll_ValidPageable_ShouldReturnAllOrders() {
        //Given
        Long userId = 1L;

        Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        Order order = createEmptyOrderSample();
        OrderDto expectedOrderDto = createEmptyOrderDtoSampleFromEntity(order);

        List<Order> orders = List.of(order);
        Page<Order> orderPage = new PageImpl<>(orders, pageable, orders.size());

        when(orderRepository.findAllByUserId(pageable, userId)).thenReturn(orderPage);
        when(orderMapper.toDto(order)).thenReturn(expectedOrderDto);

        //When
        Page<OrderDto> actucalOrderDtosPage = orderService.getAllOrders(pageable, userId);

        //Then
        assertThat(actucalOrderDtosPage).hasSize(1);
        assertThat(actucalOrderDtosPage.getContent().getFirst()).isEqualTo(expectedOrderDto);
        verify(orderRepository).findAllByUserId(pageable, userId);
        verify(orderMapper).toDto(order);
        verifyNoMoreInteractions(orderRepository, orderMapper);
    }

    @Test
    @DisplayName("""
            getOrderItems():
             Should return a list of order items for the given orderId
            """)
    void getOrderItems_ValidOrderId_ShouldReturnOrderItemsList() {
        //Given
        Long orderId = 1L;

        Order order = createEmptyOrderSample();
        Book book = createDefaultBookSample();
        OrderItem orderItem = createOrderItemSample(order, book);
        OrderItemDto expectedOrderItemDto = createOrderItemDtoSampleFromEntity(orderItem);

        when(orderRepository.findByIdWithOrderItems(orderId)).thenReturn(Optional.of(order));
        when(orderItemMapper.toDto(orderItem)).thenReturn(expectedOrderItemDto);

        //When
        List<OrderItemDto> actualOrderItemDtosList = orderService.getOrderItems(orderId);

        assertThat(actualOrderItemDtosList).hasSize(1);
        assertThat(actualOrderItemDtosList.getFirst()).isEqualTo(expectedOrderItemDto);
        verify(orderRepository).findByIdWithOrderItems(orderId);
        verify(orderItemMapper).toDto(orderItem);
        verifyNoMoreInteractions(orderRepository, orderItemMapper);
    }

    @Test
    @DisplayName("""
            getOrderItems():
             Should throw EntityNotFoundException when no order is found for the given orderId
            """)
    void getOrderItems_InvalidOrderId_ShouldThrowException() {
        //Given
        Long orderId = 1L;

        when(orderRepository.findByIdWithOrderItems(orderId)).thenReturn(Optional.empty());

        //When
        Exception exception = assertThrows(
                EntityNotFoundException.class, () -> orderService.getOrderItems(orderId)
        );

        //Then
        String expected = "Can't find order with id: " + orderId;
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(orderRepository).findByIdWithOrderItems(orderId);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    @DisplayName("""
            getOrderItems():
             Should return the correct OrderItemDto for the given orderId and orderItemId
            """)
    void getOrderItemInfo_ValidOrderIdAndOrderItemId_ReturnsOrderItemDto() {
        //Given
        Long orderId = 1L;
        Long itemId = 1L;

        Order order = createEmptyOrderSample();
        Book book = createDefaultBookSample();
        OrderItem orderItem = createOrderItemSample(order, book);
        OrderItemDto expectedOrderItemDto = createOrderItemDtoSampleFromEntity(orderItem);

        when(orderRepository.findByIdWithOrderItems(orderId)).thenReturn(Optional.of(order));
        when(orderItemMapper.toDto(orderItem)).thenReturn(expectedOrderItemDto);

        //When
        OrderItemDto actualOrderItemDto = orderService.getOrderItemInfo(orderId, itemId);

        assertThat(actualOrderItemDto).isEqualTo(expectedOrderItemDto);
        verify(orderRepository).findByIdWithOrderItems(orderId);
        verify(orderItemMapper).toDto(orderItem);
        verifyNoMoreInteractions(orderRepository, orderItemMapper);
    }

    @Test
    @DisplayName("""
            getOrderItemInfo():
             Should throw EntityNotFoundException when no order item is found for the given itemId
            """)
    void getOrderItemInfo_InvalidOrderItemId_ShouldThrowException() {
        //Given
        Long orderId = 1L;
        Long itemId = 99L;

        Order order = createEmptyOrderSample();
        when(orderRepository.findByIdWithOrderItems(orderId)).thenReturn(Optional.of(order));

        //When
        Exception exception = assertThrows(
                EntityNotFoundException.class, () -> orderService.getOrderItemInfo(orderId, itemId)
        );

        //Then
        String expected = "Can't find order item with id: " + itemId;
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(orderRepository).findByIdWithOrderItems(orderId);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    @DisplayName("""
            updateOrderStatus():
             Should return the updated OrderDto after successfully updating the order status
            """)
    void updateOrderStatus_ValidRequestDto_ReturnsOrderDto() {
        //Given
        Long orderId = 1L;
        String updatedOrderStatus = "DELIVERED";
        Order order = createEmptyOrderSample();

        UpdateOrderStatusRequestDto requestDto = new UpdateOrderStatusRequestDto(
                updatedOrderStatus);
        order.setStatus(Order.Status.valueOf(requestDto.status()));

        OrderDto expectedOrderDto = createEmptyOrderDtoSampleFromEntity(order);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(expectedOrderDto);

        //When
        OrderDto actualOrderDto = orderService.updateOrderStatus(requestDto, orderId);

        //Then
        assertEquals(actualOrderDto, expectedOrderDto);
        verify(orderRepository).findById(orderId);
        verify(orderRepository).save(order);
        verify(orderMapper).toDto(order);
        verifyNoMoreInteractions(orderRepository, orderMapper);
    }

    @Test
    @DisplayName("""
            updateOrderStatus():
             Should throw EntityNotFoundException when no order is found for the given orderId
            """)
    void updateOrderStatus_InvalidOrderId_ShouldThrowException() {
        //Given
        Long orderId = 99L;
        String updatedOrderStatus = "DELIVERED";
        UpdateOrderStatusRequestDto requestDto = new UpdateOrderStatusRequestDto(
                updatedOrderStatus);

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        //When
        Exception exception = assertThrows(
                EntityNotFoundException.class, () -> orderService.updateOrderStatus(
                        requestDto, orderId)
        );

        //Then
        String expected = "Can't find order with id: " + orderId;
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(orderRepository).findById(orderId);
        verifyNoMoreInteractions(orderRepository);
    }
}
