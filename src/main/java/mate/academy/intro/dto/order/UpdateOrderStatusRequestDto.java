package mate.academy.intro.dto.order;

import jakarta.validation.constraints.NotBlank;
import mate.academy.intro.annotations.ValidEnum;
import mate.academy.intro.model.Order;

public record UpdateOrderStatusRequestDto(
        @NotBlank
        @ValidEnum(enumClass = Order.Status.class)
        String status
) {
}
