package book.store.intro.controller;

import book.store.intro.dto.shopping.cart.AddItemToCartRequestDto;
import book.store.intro.dto.shopping.cart.ShoppingCartDto;
import book.store.intro.dto.shopping.cart.UpdateItemInCartRequestDto;
import book.store.intro.model.User;
import book.store.intro.service.shopping.cart.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping cart management", description = "Endpoints for managing shopping cart")
@SecurityRequirement(name = "BearerAuthentication")
@RequiredArgsConstructor
@RestController
@RequestMapping("/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping
    @Operation(
            summary = "Get shopping cart info",
            description = "Get all items in the shopping cart of the authenticated user"
                    + "(Required roles: USER, ADMIN)"
    )
    public ShoppingCartDto getCartInfo(Authentication authentication) {
        return shoppingCartService.getCartInfo(getAuthenticatedUserId(authentication));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PostMapping
    @Operation(
            summary = "Add new item in shopping cart",
            description = "Create a new cart item with the provided parameters and add it to the "
                    + "authenticated user's shopping cart (Required roles: ADMIN)"
    )
    public ShoppingCartDto addItemToCart(
            @RequestBody @Valid AddItemToCartRequestDto requestDto,
            Authentication authentication) {
        return shoppingCartService.addItemToCart(
                requestDto, getAuthenticatedUserId(authentication));
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PutMapping("/item/{id}")
    @Operation(
            summary = "Update shopping cart item by ID",
            description = "Update the details of a specific item in the authenticated "
                    + "user's shopping cart (Required roles: USER, ADMIN)"
    )
    public ShoppingCartDto updateItemInCart(
            @RequestBody @Valid UpdateItemInCartRequestDto requestDto,
            @PathVariable Long id,
            Authentication authentication) {
        return shoppingCartService.updateItemInCart(
                requestDto, id, getAuthenticatedUserId(authentication));
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/item/{id}")
    @Operation(
            summary = "Delete an item by ID",
            description = "Delete the item form the authenticated user's shopping cart by "
                    + "the given ID (Required roles: USER)"
    )
    public void deleteBook(@PathVariable Long id) {
        shoppingCartService.deleteItemById(id);
    }

    private Long getAuthenticatedUserId(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return user.getId();
    }
}
