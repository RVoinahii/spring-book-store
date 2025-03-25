package book.store.intro.util;

import static book.store.intro.util.TestBookDataUtil.DEFAULT_ID_SAMPLE;
import static book.store.intro.util.TestBookDataUtil.createDefaultBookSample;

import book.store.intro.dto.shopping.cart.AddItemToCartRequestDto;
import book.store.intro.dto.shopping.cart.ShoppingCartDto;
import book.store.intro.dto.shopping.cart.item.CartItemDto;
import book.store.intro.model.Book;
import book.store.intro.model.CartItem;
import book.store.intro.model.ShoppingCart;
import book.store.intro.model.User;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TestShoppingCartDataUtil {
    public static final int DEFAULT_ITEM_QUANTITY = 1;

    public static ShoppingCart createEmptyShoppingCartSample() {
        ShoppingCart shoppingCart = new ShoppingCart();
        User user = new User();
        user.setId(DEFAULT_ID_SAMPLE);
        shoppingCart.setId(user.getId());
        shoppingCart.setUser(user);
        shoppingCart.setCartItems(new HashSet<>());
        return shoppingCart;
    }

    public static ShoppingCartDto createShoppingCartDtoSampleFromEntity(ShoppingCart shoppingCart) {
        ShoppingCartDto shoppingCartDto = new ShoppingCartDto();
        shoppingCartDto.setId(shoppingCart.getId());
        shoppingCartDto.setUserId(shoppingCart.getUser().getId());
        shoppingCartDto.setCartItems(new ArrayList<>());
        return shoppingCartDto;
    }

    public static AddItemToCartRequestDto createAddItemToCartRequestDtoSample() {
        return new AddItemToCartRequestDto(DEFAULT_ID_SAMPLE, DEFAULT_ITEM_QUANTITY);
    }

    public static CartItem createCartItemSample(ShoppingCart shoppingCart, Book book) {
        CartItem cartItem = new CartItem();
        cartItem.setId(DEFAULT_ID_SAMPLE);
        cartItem.setShoppingCart(shoppingCart);
        shoppingCart.setCartItems(new HashSet<>(Set.of(cartItem)));
        cartItem.setBook(book);
        cartItem.setQuantity(DEFAULT_ITEM_QUANTITY);
        return cartItem;
    }

    public static CartItemDto createCartItemDtoSample(CartItem cartItem) {
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setId(cartItem.getId());
        cartItemDto.setBookId(cartItem.getBook().getId());
        cartItemDto.setBookTitle(cartItem.getBook().getTitle());
        cartItemDto.setQuantity(cartItem.getQuantity());
        return cartItemDto;
    }

    public static CartItemDto createDefaultCartItemDtoSample() {
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setId(DEFAULT_ID_SAMPLE);

        Book book = createDefaultBookSample();

        cartItemDto.setBookId(book.getId());
        cartItemDto.setBookTitle(book.getTitle());
        cartItemDto.setQuantity(DEFAULT_ITEM_QUANTITY);
        return cartItemDto;
    }
}
