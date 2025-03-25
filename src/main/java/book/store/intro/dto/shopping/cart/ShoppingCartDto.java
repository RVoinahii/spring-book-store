package book.store.intro.dto.shopping.cart;

import book.store.intro.dto.shopping.cart.item.CartItemDto;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShoppingCartDto {
    private Long id;
    private Long userId;
    private List<CartItemDto> cartItems;
}
