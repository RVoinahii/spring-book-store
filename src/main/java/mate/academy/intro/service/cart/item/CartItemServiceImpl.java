package mate.academy.intro.service.cart.item;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import mate.academy.intro.dto.shopping.cart.AddItemToCartRequestDto;
import mate.academy.intro.dto.shopping.cart.UpdateItemInCartRequestDto;
import mate.academy.intro.exceptions.EntityNotFoundException;
import mate.academy.intro.model.Book;
import mate.academy.intro.model.CartItem;
import mate.academy.intro.model.ShoppingCart;
import mate.academy.intro.repository.book.BookRepository;
import mate.academy.intro.repository.cart.item.CartItemRepository;
import mate.academy.intro.repository.shopping.cart.ShoppingCartRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CartItemServiceImpl implements CartItemService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final BookRepository bookRepository;
    private final CartItemRepository cartItemRepository;
    
    @Override
    public CartItem create(AddItemToCartRequestDto requestDto, Long userId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByOwnerId(userId).orElseThrow(
                () -> new EntityNotFoundException("Can't find shopping cart "
                        + "for user"));
        Book book = bookRepository.findById(requestDto.bookId()).orElseThrow(
                () -> new EntityNotFoundException("Can't find book by id: " + requestDto.bookId()));

        Optional<CartItem> optionalCartItem = cartItemRepository.findByShoppingCartIdAndBookId(
                shoppingCart.getId(), book.getId());

        if (optionalCartItem.isPresent()) {
            CartItem existingItem = optionalCartItem.get();
            existingItem.setBook(book);
            existingItem.setQuantity(existingItem.getQuantity() + requestDto.quantity());
            return cartItemRepository.save(existingItem);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setShoppingCart(shoppingCart);
            cartItem.setBook(book);
            cartItem.setQuantity(requestDto.quantity());
            return cartItemRepository.save(cartItem);
        }
    }

    @Override
    public CartItem update(UpdateItemInCartRequestDto requestDto, Long id) {
        CartItem cartItem = cartItemRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find cart item with id: " + id));
        cartItem.setQuantity(requestDto.quantity());
        return cartItemRepository.save(cartItem);
    }

    @Override
    public void deleteById(Long id) {
        cartItemRepository.deleteById(id);
    }
}
