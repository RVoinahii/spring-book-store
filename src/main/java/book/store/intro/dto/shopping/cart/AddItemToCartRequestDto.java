package book.store.intro.dto.shopping.cart;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AddItemToCartRequestDto(
        @Positive
        @NotNull
        Long bookId,

        @Positive
        int quantity
) {
}
