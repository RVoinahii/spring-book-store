package mate.academy.intro.dto.shopping.cart;

import jakarta.validation.constraints.Positive;

public record AddItemToCartRequestDto(
        @Positive
        Long bookId,

        @Positive
        int quantity
) {
}
