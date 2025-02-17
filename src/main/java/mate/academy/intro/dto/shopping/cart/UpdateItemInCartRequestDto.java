package mate.academy.intro.dto.shopping.cart;

import jakarta.validation.constraints.Positive;

public record UpdateItemInCartRequestDto(
        @Positive
        int quantity
) {
}
