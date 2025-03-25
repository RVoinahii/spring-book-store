package book.store.intro.service.shopping.cart;

import book.store.intro.dto.shopping.cart.AddItemToCartRequestDto;
import book.store.intro.dto.shopping.cart.ShoppingCartDto;
import book.store.intro.dto.shopping.cart.UpdateItemInCartRequestDto;
import book.store.intro.model.ShoppingCart;
import book.store.intro.model.User;

public interface ShoppingCartService {
    ShoppingCartDto getCartInfo(Long userId);

    ShoppingCartDto addItemToCart(AddItemToCartRequestDto requestDto, Long userId);

    ShoppingCartDto updateItemInCart(UpdateItemInCartRequestDto requestDto, Long id, Long userId);

    void deleteItemById(Long id);

    ShoppingCart findShoppingCartByUserId(Long userId);

    void createShoppingCartForUser(User user);
}
