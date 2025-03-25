package book.store.intro.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateOrderRequestDto(
        @NotBlank
        @Size(max = 255)
        String shippingAddress
) {
}
