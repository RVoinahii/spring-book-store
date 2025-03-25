package book.store.intro.controller;

import static book.store.intro.util.TestShoppingCartDataUtil.createAddItemToCartRequestDtoSample;
import static book.store.intro.util.TestShoppingCartDataUtil.createDefaultCartItemDtoSample;
import static book.store.intro.util.TestUserDataUtil.USER_EMAIL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import book.store.intro.dto.shopping.cart.AddItemToCartRequestDto;
import book.store.intro.dto.shopping.cart.ShoppingCartDto;
import book.store.intro.dto.shopping.cart.UpdateItemInCartRequestDto;
import book.store.intro.dto.shopping.cart.item.CartItemDto;
import book.store.intro.service.shopping.cart.ShoppingCartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ShoppingCartControllerTests {
    protected static MockMvc mockMvc;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserDetailsService userDetailsService;

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext applicationContext
    ) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @WithUserDetails(value = USER_EMAIL, userDetailsServiceBeanName = "customUserDetailsService")
    @Test
    @DisplayName("""
            getCartInfo():
             Verifying retrieval of full shopping cart info with correct user authentication
            """)
    @Sql(scripts = {
            "classpath:database/users/insert_one_user.sql",
            "classpath:database/users_roles/set_user_one_roles_USER.sql",
            "classpath:database/shopping_carts/insert_one_shopping_cart.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/clear_database.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getCartInfo_ValidUser_Success() throws Exception {
        //Given
        Long expectedId = 3L;

        ShoppingCartDto expected = new ShoppingCartDto();
        expected.setId(expectedId);
        expected.setUserId(expectedId);
        expected.setCartItems(new ArrayList<>());

        //When
        MvcResult result = mockMvc.perform(get("/cart"))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        ShoppingCartDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), ShoppingCartDto.class);
        assertNotNull(actual);
        assertEquals(expectedId, actual.getId());
        assertEquals(expectedId, actual.getUserId());
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @Test
    @WithUserDetails(value = USER_EMAIL, userDetailsServiceBeanName = "customUserDetailsService")
    @DisplayName("""
            addItemToCart():
             Confirming successful creation of new cart item with valid request
            """)
    @Sql(scripts = {
            "classpath:database/users/insert_one_user.sql",
            "classpath:database/users_roles/set_user_one_roles_USER.sql",
            "classpath:database/shopping_carts/insert_one_shopping_cart.sql",
            "classpath:database/books/insert_one_book.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/clear_database.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void addItemToCart_ValidRequestDtoAndUser_Created() throws Exception {
        //Given
        AddItemToCartRequestDto requestDto = createAddItemToCartRequestDtoSample();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        Long expectedId = 3L;

        CartItemDto expectedCartItemDto = createDefaultCartItemDtoSample();

        //When
        MvcResult result = mockMvc.perform(
                post("/cart")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        //Then
        ShoppingCartDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), ShoppingCartDto.class);
        assertNotNull(actual);
        assertTrue(EqualsBuilder.reflectionEquals(expectedCartItemDto,
                actual.getCartItems().getFirst()));
        assertEquals(expectedId, actual.getId());
        assertEquals(expectedId, actual.getUserId());
    }

    @Test
    @WithUserDetails(value = USER_EMAIL, userDetailsServiceBeanName = "customUserDetailsService")
    @DisplayName("""
            addItemToCart():
             Should return 400 BAD REQUEST when given invalid request body
            """)
    @Sql(scripts = {
            "classpath:database/users/insert_one_user.sql",
            "classpath:database/users_roles/set_user_one_roles_USER.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/clear_database.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void addItemToCart_InvalidRequestDto_BadRequest() throws Exception {
        //Given
        Long expectedId = 3L;

        AddItemToCartRequestDto requestDto = new AddItemToCartRequestDto(null, 1);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        CartItemDto expectedCartItemDto = createDefaultCartItemDtoSample();

        //When & Then
        MvcResult result = mockMvc.perform(
                        post("/cart")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    @WithUserDetails(value = USER_EMAIL, userDetailsServiceBeanName = "customUserDetailsService")
    @DisplayName("""
            updateItemInCart():
             Verifying updating cart item quantity data by ID with valid request
            """)
    @Sql(scripts = {
            "classpath:database/users/insert_one_user.sql",
            "classpath:database/users_roles/set_user_one_roles_USER.sql",
            "classpath:database/shopping_carts/insert_one_shopping_cart.sql",
            "classpath:database/books/insert_one_book.sql",
            "classpath:database/cart_items/insert_one_cart_item.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/clear_database.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateItemInCart_ValidRequestDtoAndItemId_Success() throws Exception {
        //Given
        Long expectedId = 3L;
        Long cartItemId = 1L;

        UpdateItemInCartRequestDto requestDto = new UpdateItemInCartRequestDto(5);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        CartItemDto expectedCartItemDto = createDefaultCartItemDtoSample();
        expectedCartItemDto.setQuantity(5);

        //When
        MvcResult result = mockMvc.perform(
                        put("/cart/item/{cartItemId}", cartItemId)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        //Then
        ShoppingCartDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), ShoppingCartDto.class);
        assertNotNull(actual);
        assertEquals(expectedId, actual.getId());
        assertEquals(expectedId, actual.getUserId());
        assertTrue(EqualsBuilder.reflectionEquals(expectedCartItemDto,
                actual.getCartItems().getFirst()));
    }

    @Test
    @WithUserDetails(value = USER_EMAIL, userDetailsServiceBeanName = "customUserDetailsService")
    @DisplayName("""
            updateItemInCart():
             Should return 404 NOT FOUND when given invalid ID
            """)
    @Sql(scripts = {
            "classpath:database/users/insert_one_user.sql",
            "classpath:database/users_roles/set_user_one_roles_USER.sql",
            "classpath:database/shopping_carts/insert_one_shopping_cart.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/clear_database.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateItemInCart_InvalidItemId_NotFound() throws Exception {
        //Given
        Long cartItemId = 1L;

        UpdateItemInCartRequestDto requestDto = new UpdateItemInCartRequestDto(5);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When & Then
        MvcResult result = mockMvc.perform(
                        put("/cart/item/{cartItemId}", cartItemId)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    @WithUserDetails(value = USER_EMAIL, userDetailsServiceBeanName = "customUserDetailsService")
    @DisplayName("""
            deleteBook():
             Verifying successful cart item removal by its ID
            """)
    @Sql(scripts = {
            "classpath:database/users/insert_one_user.sql",
            "classpath:database/users_roles/set_user_one_roles_USER.sql",
            "classpath:database/shopping_carts/insert_one_shopping_cart.sql",
            "classpath:database/books/insert_one_book.sql",
            "classpath:database/cart_items/insert_one_cart_item.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/clear_database.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteBook_ValidItemIdAndUser_NoContent() throws Exception {
        //Given
        Long expectedId = 3L;
        Long cartItemId = 1L;

        //When
        MvcResult result = mockMvc.perform(delete("/cart/item/{cartItemId}", cartItemId))
                .andExpect(status().isNoContent())
                .andReturn();

        //Then
        MvcResult expected = mockMvc.perform(get("/cart"))
                .andExpect(status().isOk())
                .andReturn();

        ShoppingCartDto actual = objectMapper.readValue(expected.getResponse()
                .getContentAsString(), ShoppingCartDto.class);
        assertNotNull(actual);
        assertEquals(expectedId, actual.getId());
        assertEquals(expectedId, actual.getUserId());
        assertTrue(actual.getCartItems().isEmpty());
    }
}
