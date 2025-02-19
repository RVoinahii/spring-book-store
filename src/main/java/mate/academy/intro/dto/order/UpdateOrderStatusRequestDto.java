package mate.academy.intro.dto.order;

import jakarta.validation.constraints.NotBlank;
import mate.academy.intro.annotations.OrderStatus;
import mate.academy.intro.model.Order;

public record UpdateOrderStatusRequestDto(
        @NotBlank
        @OrderStatus(enumClass = Order.Status.class)
        String status
) {
}
