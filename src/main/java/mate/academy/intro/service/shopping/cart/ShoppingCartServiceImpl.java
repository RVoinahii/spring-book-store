package mate.academy.intro.service.shopping.cart;

import lombok.RequiredArgsConstructor;
import mate.academy.intro.dto.shopping.cart.AddItemToCartRequestDto;
import mate.academy.intro.dto.shopping.cart.ShoppingCartDto;
import mate.academy.intro.dto.shopping.cart.UpdateItemInCartRequestDto;
import mate.academy.intro.exceptions.EntityNotFoundException;
import mate.academy.intro.mapper.ShoppingCartMapper;
import mate.academy.intro.model.Book;
import mate.academy.intro.model.CartItem;
import mate.academy.intro.model.ShoppingCart;
import mate.academy.intro.model.User;
import mate.academy.intro.repository.book.BookRepository;
import mate.academy.intro.repository.cart.item.CartItemRepository;
import mate.academy.intro.repository.shopping.cart.ShoppingCartRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;

    @Override
    public ShoppingCartDto getCartInfo(Long userId) {
        ShoppingCart shoppingCart = findShoppingCartByUserId(userId);
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public ShoppingCartDto addItemToCart(AddItemToCartRequestDto requestDto, Long userId) {
        ShoppingCart shoppingCart = findShoppingCartByUserId(userId);
        Book book = bookRepository.findById(requestDto.bookId()).orElseThrow(
                () -> new EntityNotFoundException("Can't find book by id: " + requestDto.bookId()));
        CartItem existingCartItem = shoppingCart.getCartItems().stream()
                .filter(item -> item.getBook().getId().equals(requestDto.bookId()))
                .findFirst()
                .orElse(null);
        if (existingCartItem == null) {
            CartItem cartItem = createCartItem(shoppingCart, book, requestDto.quantity());
            shoppingCart.getCartItems().add(cartItem);
        } else {
            existingCartItem.setQuantity(existingCartItem.getQuantity() + requestDto.quantity());
            cartItemRepository.save(existingCartItem);
        }
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public ShoppingCartDto updateItemInCart(
            UpdateItemInCartRequestDto requestDto, Long id, Long userId) {
        ShoppingCart shoppingCart = findShoppingCartByUserId(userId);
        CartItem existingCartItem = shoppingCart.getCartItems().stream()
                .filter(item -> item.getId().equals(id))
                .findFirst()
                .orElseThrow(
                        () -> new EntityNotFoundException("Can't find cart item with id: " + id));
        existingCartItem.setQuantity(requestDto.quantity());
        cartItemRepository.save(existingCartItem);
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public void createShoppingCartForUser(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public void deleteItemById(Long id) {
        cartItemRepository.deleteById(id);
    }

    private ShoppingCart findShoppingCartByUserId(Long userId) {
        return shoppingCartRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Can't find shopping cart "
                        + "for user"));
    }

    private CartItem createCartItem(ShoppingCart shoppingCart, Book book, int quantity) {
        CartItem cartItem = new CartItem();
        cartItem.setShoppingCart(shoppingCart);
        cartItem.setBook(book);
        cartItem.setQuantity(quantity);
        return cartItemRepository.save(cartItem);
    }
}
