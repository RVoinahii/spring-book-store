package mate.academy.intro.service.shopping.cart;

import mate.academy.intro.dto.shopping.cart.AddItemToCartRequestDto;
import mate.academy.intro.dto.shopping.cart.ShoppingCartDto;
import mate.academy.intro.dto.shopping.cart.UpdateItemInCartRequestDto;
import mate.academy.intro.model.User;

public interface ShoppingCartService {
    ShoppingCartDto getCartInfo(Long userId);

    ShoppingCartDto addItemToCart(AddItemToCartRequestDto requestDto, Long userId);

    ShoppingCartDto updateItemInCart(UpdateItemInCartRequestDto requestDto, Long id, Long userId);

    void createShoppingCartForUser(User user);
}
