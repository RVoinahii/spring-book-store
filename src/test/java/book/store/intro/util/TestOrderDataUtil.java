package book.store.intro.util;

import static book.store.intro.util.TestBookDataUtil.DEFAULT_ID_SAMPLE;
import static book.store.intro.util.TestShoppingCartDataUtil.DEFAULT_ITEM_QUANTITY;

import book.store.intro.dto.order.OrderDto;
import book.store.intro.dto.order.item.OrderItemDto;
import book.store.intro.model.Book;
import book.store.intro.model.Order;
import book.store.intro.model.Order.Status;
import book.store.intro.model.OrderItem;
import book.store.intro.model.User;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TestOrderDataUtil {
    public static final BigDecimal ORDER_ITEM_DEFAULT_PRICE = BigDecimal.ONE;
    public static final BigDecimal ORDER_DEFAULT_TOTAL = new BigDecimal("0.00");
    public static final String ORDER_SHIPPING_ADDRESS = "Shipping address";

    public static Order createEmptyOrderSample() {
        Order order = new Order();
        order.setId(DEFAULT_ID_SAMPLE);

        User user = new User();
        user.setId(DEFAULT_ID_SAMPLE);

        order.setUser(user);
        order.setStatus(Status.PENDING);
        order.setTotal(ORDER_DEFAULT_TOTAL);
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(ORDER_SHIPPING_ADDRESS);
        order.setOrderItems(new HashSet<>());
        return order;
    }

    public static OrderDto createEmptyOrderDtoSampleFromEntity(Order order) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setUserId(order.getUser().getId());
        orderDto.setOrderItems(new ArrayList<>());
        orderDto.setOrderDate(order.getOrderDate());
        orderDto.setTotal(order.getTotal());
        orderDto.setStatus(String.valueOf(order.getStatus()));
        return orderDto;
    }

    public static OrderDto createDefaultEmptyOrderDtoSample() {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(DEFAULT_ID_SAMPLE);
        orderDto.setUserId(DEFAULT_ID_SAMPLE);
        orderDto.setOrderItems(new ArrayList<>());
        orderDto.setOrderDate(LocalDateTime.now());
        orderDto.setTotal(ORDER_DEFAULT_TOTAL);
        orderDto.setStatus(String.valueOf(Status.PENDING));
        return orderDto;
    }

    public static OrderItem createOrderItemSample(Order order, Book book) {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(DEFAULT_ID_SAMPLE);

        orderItem.setOrder(order);
        order.setOrderItems(new HashSet<>(Set.of(orderItem)));

        orderItem.setBook(book);
        orderItem.setQuantity(DEFAULT_ITEM_QUANTITY);
        orderItem.setPrice(ORDER_ITEM_DEFAULT_PRICE);
        order.setTotal(order.getTotal().add(
                orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()))));
        return orderItem;
    }

    public static OrderItemDto createOrderItemDtoSampleFromEntity(OrderItem orderItem) {
        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setId(orderItem.getId());
        orderItemDto.setBookId(orderItem.getBook().getId());
        orderItemDto.setQuantity(orderItemDto.getQuantity());
        return orderItemDto;
    }

    public static OrderItemDto createDefaultOrderItemDtoSample() {
        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setId(DEFAULT_ID_SAMPLE);
        orderItemDto.setBookId(DEFAULT_ID_SAMPLE);
        orderItemDto.setQuantity(DEFAULT_ITEM_QUANTITY);
        return orderItemDto;
    }

}
