package mate.academy.intro.service.cart.item;

import mate.academy.intro.dto.shopping.cart.AddItemToCartRequestDto;
import mate.academy.intro.dto.shopping.cart.UpdateItemInCartRequestDto;
import mate.academy.intro.model.CartItem;

public interface CartItemService {
    CartItem create(AddItemToCartRequestDto requestDto, Long userId);

    CartItem update(UpdateItemInCartRequestDto requestDto, Long id);

    void deleteById(Long id);
}
