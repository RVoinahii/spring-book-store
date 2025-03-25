package book.store.intro.dto.order;

import book.store.intro.annotations.ValidEnum;
import book.store.intro.model.Order;
import jakarta.validation.constraints.NotBlank;

public record UpdateOrderStatusRequestDto(
        @NotBlank
        @ValidEnum(enumClass = Order.Status.class)
        String status
) {
}
