package book.store.intro.service.shopping.cart;

import static book.store.intro.util.TestBookDataUtil.createDefaultBookSample;
import static book.store.intro.util.TestShoppingCartDataUtil.createAddItemToCartRequestDtoSample;
import static book.store.intro.util.TestShoppingCartDataUtil.createCartItemDtoSample;
import static book.store.intro.util.TestShoppingCartDataUtil.createCartItemSample;
import static book.store.intro.util.TestShoppingCartDataUtil.createEmptyShoppingCartSample;
import static book.store.intro.util.TestShoppingCartDataUtil.createShoppingCartDtoSampleFromEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import book.store.intro.dto.shopping.cart.AddItemToCartRequestDto;
import book.store.intro.dto.shopping.cart.ShoppingCartDto;
import book.store.intro.dto.shopping.cart.UpdateItemInCartRequestDto;
import book.store.intro.dto.shopping.cart.item.CartItemDto;
import book.store.intro.exceptions.EntityNotFoundException;
import book.store.intro.mapper.ShoppingCartMapper;
import book.store.intro.model.Book;
import book.store.intro.model.CartItem;
import book.store.intro.model.ShoppingCart;
import book.store.intro.model.User;
import book.store.intro.repository.book.BookRepository;
import book.store.intro.repository.shopping.cart.ShoppingCartRepository;
import book.store.intro.repository.shopping.cart.item.CartItemRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartServiceTests {
    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @Mock
    private ShoppingCartMapper shoppingCartMapper;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Test
    @DisplayName("""
            getCartInfo():
             Should return the shopping cart DTO for a valid user ID
            """)
    void getCartInfo_validUserId_ReturnsShoppingCartDto() {
        //Given
        Long userId = 1L;

        ShoppingCart shoppingCart = createEmptyShoppingCartSample();
        ShoppingCartDto expectedShoppingCartDto = createShoppingCartDtoSampleFromEntity(
                shoppingCart);

        when(shoppingCartRepository.findById(userId)).thenReturn(Optional.of(shoppingCart));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expectedShoppingCartDto);

        //When
        ShoppingCartDto actualShoppingCartDto = shoppingCartService.getCartInfo(userId);

        //Then
        assertThat(actualShoppingCartDto).isEqualTo(expectedShoppingCartDto);
        verify(shoppingCartRepository).findById(userId);
        verify(shoppingCartMapper).toDto(shoppingCart);
        verifyNoMoreInteractions(shoppingCartRepository, shoppingCartMapper);
    }

    @Test
    @DisplayName("""
            getCartInfo():
             Should throw an exception when the user ID is invalid
            """)
    void getCartInfo_invalidUserId_ShouldThrowException() {
        //Given
        Long userId = 1L;

        when(shoppingCartRepository.findById(userId)).thenReturn(Optional.empty());

        //When
        Exception exception = assertThrows(
                EntityNotFoundException.class, () -> shoppingCartService.getCartInfo(userId)
        );

        //Given
        String expected = "Can't find shopping cart for user";
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(shoppingCartRepository).findById(userId);
        verifyNoMoreInteractions(shoppingCartRepository);
    }

    @Test
    @DisplayName("""
            addItemToCart():
             Should update the quantity of an existing item in the cart
            """)
    void addItemToCart_ExistingItem_UpdatesQuantity() {
        //Given
        Long userId = 1L;

        AddItemToCartRequestDto requestDto = createAddItemToCartRequestDtoSample();
        Book book = createDefaultBookSample();
        ShoppingCart shoppingCart = createEmptyShoppingCartSample();
        CartItem cartItem = createCartItemSample(shoppingCart, book);
        CartItemDto cartItemDto = createCartItemDtoSample(cartItem);

        ShoppingCartDto expectedShoppingCartDto = createShoppingCartDtoSampleFromEntity(
                shoppingCart);
        expectedShoppingCartDto.setCartItems(List.of(cartItemDto));

        when(shoppingCartRepository.findById(userId)).thenReturn(Optional.of(shoppingCart));
        when(bookRepository.findById(requestDto.bookId())).thenReturn(Optional.of(book));
        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expectedShoppingCartDto);

        //When
        ShoppingCartDto actualShoppingCartDto = shoppingCartService.addItemToCart(
                requestDto, userId);

        //Then
        assertThat(actualShoppingCartDto).isEqualTo(expectedShoppingCartDto);
        verify(shoppingCartRepository).findById(userId);
        verify(bookRepository).findById(requestDto.bookId());
        verify(cartItemRepository).save(cartItem);
        verify(shoppingCartMapper).toDto(shoppingCart);
        verifyNoMoreInteractions(shoppingCartRepository, bookRepository,
                cartItemRepository, shoppingCartMapper);
    }

    @Test
    @DisplayName("""
            addItemToCart():
             Should add a new item to the shopping cart
            """)
    void addItemToCart_NewItem_AddsToCart() {
        Long cartItemId = 1L;

        AddItemToCartRequestDto requestDto = createAddItemToCartRequestDtoSample();
        Book book = createDefaultBookSample();
        ShoppingCart shoppingCart = createEmptyShoppingCartSample();

        CartItem newCartItem = new CartItem();
        newCartItem.setId(cartItemId);
        newCartItem.setShoppingCart(shoppingCart);
        newCartItem.setBook(book);
        newCartItem.setQuantity(requestDto.quantity());
        CartItemDto newCartItemDto = createCartItemDtoSample(newCartItem);

        ShoppingCartDto expectedShoppingCartDto = createShoppingCartDtoSampleFromEntity(
                shoppingCart);
        expectedShoppingCartDto.setCartItems(new ArrayList<>(List.of(newCartItemDto)));

        Long userId = 1L;

        when(shoppingCartRepository.findById(userId)).thenReturn(Optional.of(shoppingCart));
        when(bookRepository.findById(requestDto.bookId())).thenReturn(Optional.of(book));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(newCartItem);
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expectedShoppingCartDto);

        //When
        ShoppingCartDto actualShoppingCartDto = shoppingCartService.addItemToCart(
                requestDto, userId);

        //Then
        assertThat(actualShoppingCartDto).isEqualTo(expectedShoppingCartDto);
        verify(shoppingCartRepository).findById(userId);
        verify(bookRepository).findById(requestDto.bookId());
        verify(cartItemRepository).save(any(CartItem.class));
        verify(shoppingCartMapper).toDto(shoppingCart);
        verifyNoMoreInteractions(shoppingCartRepository, bookRepository,
                cartItemRepository, shoppingCartMapper);
    }

    @Test
    @DisplayName("""
            addItemToCart():
             Should throw an exception when the book ID is invalid
            """)
    void addItemToCart_InvalidBookId_ShouldThrowException() {
        //Given
        Long userId = 1L;

        AddItemToCartRequestDto requestDto = createAddItemToCartRequestDtoSample();
        ShoppingCart shoppingCart = createEmptyShoppingCartSample();

        when(shoppingCartRepository.findById(userId)).thenReturn(Optional.of(shoppingCart));
        when(bookRepository.findById(requestDto.bookId())).thenReturn(Optional.empty());

        //When
        Exception exception = assertThrows(
                EntityNotFoundException.class, () -> shoppingCartService.addItemToCart(
                        requestDto, userId)
        );

        //Given
        String expected = "Can't find book by id: " + requestDto.bookId();
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(shoppingCartRepository).findById(userId);
        verify(bookRepository).findById(requestDto.bookId());
        verifyNoMoreInteractions(shoppingCartRepository, bookRepository);
    }

    @Test
    @DisplayName("""
            updateItemInCart():
             Should return shopping cart DTO after updating an item with valid request and item ID
            """)
    void updateItemInCart_ValidRequestDtoAndItemId_ReturnsShoppingCartDto() {
        //Given
        int newQuantity = 5;
        Long userId = 1L;

        UpdateItemInCartRequestDto requestDto = new UpdateItemInCartRequestDto(newQuantity);
        ShoppingCart shoppingCart = createEmptyShoppingCartSample();
        Book book = createDefaultBookSample();
        CartItem cartItem = createCartItemSample(shoppingCart, book);
        cartItem.setQuantity(requestDto.quantity());
        CartItemDto cartItemDto = createCartItemDtoSample(cartItem);
        ShoppingCartDto expectedShoppingCartDto = createShoppingCartDtoSampleFromEntity(
                shoppingCart);
        expectedShoppingCartDto.setCartItems(List.of(cartItemDto));

        Long itemId = 1L;

        when(shoppingCartRepository.findById(userId)).thenReturn(Optional.of(shoppingCart));
        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expectedShoppingCartDto);

        //When
        ShoppingCartDto actualShoppingCartDto = shoppingCartService.updateItemInCart(
                requestDto, itemId, userId);

        //Then
        assertThat(actualShoppingCartDto).isEqualTo(expectedShoppingCartDto);
        verify(shoppingCartRepository).findById(userId);
        verify(cartItemRepository).save(cartItem);
        verify(shoppingCartMapper).toDto(shoppingCart);
        verifyNoMoreInteractions(shoppingCartRepository, cartItemRepository, shoppingCartMapper);
    }

    @Test
    @DisplayName("""
            updateItemInCart():
             Should throw an exception when the item ID is invalid
            """)
    void updateItemInCart_InvalidItemId_ShouldThrowException() {
        //Given
        int newQuantity = 5;
        Long userId = 1L;
        Long invalidItemId = 99L;

        UpdateItemInCartRequestDto requestDto = new UpdateItemInCartRequestDto(newQuantity);
        ShoppingCart shoppingCart = createEmptyShoppingCartSample();

        when(shoppingCartRepository.findById(userId)).thenReturn(Optional.of(shoppingCart));

        //When
        Exception exception = assertThrows(
                EntityNotFoundException.class, () -> shoppingCartService.updateItemInCart(
                        requestDto, invalidItemId, userId)
        );

        //Given
        String expected = "Can't find cart item with id: " + invalidItemId;
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(shoppingCartRepository).findById(userId);
        verifyNoMoreInteractions(shoppingCartRepository);
    }

    @Test
    @DisplayName("""
            createShoppingCartForUser():
             Should invoke the repository to create a shopping cart for a valid user
            """)
    void createShoppingCartForUser_ValidUser_ShouldInvokeRepositoryOnce() {
        //When
        shoppingCartService.createShoppingCartForUser(any(User.class));

        //Then
        verify(shoppingCartRepository).save(any(ShoppingCart.class));
        verifyNoMoreInteractions(shoppingCartRepository);
    }

    @Test
    @DisplayName("""
            deleteItemById():
             Should invoke the repository to delete an item by its ID
            """)
    void deleteItemById_ValidItemId_ShouldInvokeRepositoryOnce() {
        //Given
        Long cartItemId = 1L;

        //When
        shoppingCartService.deleteItemById(cartItemId);

        //Then
        verify(cartItemRepository).deleteById(cartItemId);
        verifyNoMoreInteractions(cartItemRepository);
    }
}
