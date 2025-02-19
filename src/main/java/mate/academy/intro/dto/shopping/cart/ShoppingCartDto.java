package mate.academy.intro.dto.shopping.cart;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import mate.academy.intro.dto.shopping.cart.item.CartItemDto;

@Getter
@Setter
public class ShoppingCartDto {
    private Long id;
    private Long userId;
    private List<CartItemDto> cartItems;
}
