package book.store.intro.controller;

import book.store.intro.dto.order.CreateOrderRequestDto;
import book.store.intro.dto.order.OrderDto;
import book.store.intro.dto.order.UpdateOrderStatusRequestDto;
import book.store.intro.dto.order.item.OrderItemDto;
import book.store.intro.model.User;
import book.store.intro.service.order.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order management", description = "Endpoints for managing orders")
@SecurityRequirement(name = "BearerAuthentication")
@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController {
    private static final String ORDER_DATE = "orderDate";

    private final OrderService orderService;

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping
    @Operation(
            summary = "Get full order list",
            description = "Retrieve all placed orders of the authenticated user "
                    + "(Required roles: USER, ADMIN)"
    )
    public Page<OrderDto> viewOrderHistory(@ParameterObject @PageableDefault(sort = ORDER_DATE,
            direction = Sort.Direction.DESC) Pageable pageable, Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        return orderService.getAllOrders(pageable, user.getId());
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/{orderId}/items")
    @Operation(
            summary = "Get all items in order ID",
            description = "Retrieve a list of all items in the specified order"
                    + "(Required roles: USER, ADMIN)"
    )
    public List<OrderItemDto> viewOrderItems(@PathVariable Long orderId) {
        return orderService.getOrderItems(orderId);
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/{orderId}/items/{itemId}")
    @Operation(
            summary = "Get info about item in order by item ID and order ID",
            description = "Retrieve details of a specific item in the specified order "
                    + "(Required roles: USER, ADMIN)"
    )
    public OrderItemDto viewOrderItem(@PathVariable Long orderId, @PathVariable Long itemId) {
        return orderService.getOrderItemInfo(orderId, itemId);
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PostMapping
    @Operation(
            summary = "Place new order",
            description = "Create a new order for the authenticated user "
                    + "(Required roles: USER, ADMIN)"
    )
    public OrderDto placeOrder(
            @RequestBody @Valid CreateOrderRequestDto requestDto, Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        return orderService.placeOrder(requestDto, user);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{orderId}")
    @Operation(
            summary = "Update order status by order ID",
            description = "Change status of an existing order by its ID "
                    + "(Required roles: ADMIN)"
    )
    public OrderDto updateOrderStatus(
            @RequestBody @Valid UpdateOrderStatusRequestDto requestDto,
            @PathVariable Long orderId) {
        return orderService.updateOrderStatus(requestDto, orderId);
    }

    private User getAuthenticatedUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }
}
