package book.store.intro.controller;

import static book.store.intro.util.TestBookDataUtil.PAGE_SIZE;
import static book.store.intro.util.TestOrderDataUtil.ORDER_SHIPPING_ADDRESS;
import static book.store.intro.util.TestOrderDataUtil.createDefaultEmptyOrderDtoSample;
import static book.store.intro.util.TestOrderDataUtil.createDefaultOrderItemDtoSample;
import static book.store.intro.util.TestUserDataUtil.USER_EMAIL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import book.store.intro.dto.order.CreateOrderRequestDto;
import book.store.intro.dto.order.OrderDto;
import book.store.intro.dto.order.UpdateOrderStatusRequestDto;
import book.store.intro.dto.order.item.OrderItemDto;
import book.store.intro.model.PageResponse;
import book.store.intro.service.order.OrderService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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
public class OrderControllerTests {
    protected static MockMvc mockMvc;

    @Autowired
    private OrderService orderService;

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
            viewOrderItem():
             Verifying retrieval of all orders with correct pagination parameters
            """)
    @Sql(scripts = {
            "classpath:database/users/insert_one_user.sql",
            "classpath:database/users_roles/set_user_one_roles_USER.sql",
            "classpath:database/books/insert_one_book.sql",
            "classpath:database/orders/insert_one_order.sql",
            "classpath:database/order_items/insert_one_order_item.sql",
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/clear_database.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void viewOrderHistory_ValidPageableAndUser_Success() throws Exception {
        //Given
        Long expectedUserId = 3L;

        OrderDto expectedOrderDto = createDefaultEmptyOrderDtoSample();
        expectedOrderDto.setUserId(expectedUserId);

        OrderItemDto expectedOrderItemDto = createDefaultOrderItemDtoSample();

        expectedOrderDto.setOrderItems(new ArrayList<>(List.of(expectedOrderItemDto)));

        Long expectedOrderId = 1L;

        //When
        MvcResult result = mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        PageResponse<OrderDto> actualOrderDtosPage = objectMapper.readValue(result.getResponse()
                .getContentAsString(), new TypeReference<>() {
                });

        assertNotNull(actualOrderDtosPage);
        assertEquals(expectedOrderId, actualOrderDtosPage.getContent().getFirst().getId());
        assertEquals(expectedUserId, actualOrderDtosPage.getContent().getFirst().getUserId());
        assertEquals(1, actualOrderDtosPage.getTotalElements());
        assertEquals(PAGE_SIZE, actualOrderDtosPage.getSize());
        assertTrue(EqualsBuilder.reflectionEquals(expectedOrderDto,
                actualOrderDtosPage.getContent().getFirst(), "orderItems", "orderDate"));
        assertTrue(EqualsBuilder.reflectionEquals(expectedOrderItemDto,
                actualOrderDtosPage.getContent().getFirst().getOrderItems().getFirst()));
    }

    @WithUserDetails(value = USER_EMAIL, userDetailsServiceBeanName = "customUserDetailsService")
    @Test
    @DisplayName("""
            viewOrderItems():
             Verifying retrieval full list of order items by order ID
            """)
    @Sql(scripts = {
            "classpath:database/users/insert_one_user.sql",
            "classpath:database/users_roles/set_user_one_roles_USER.sql",
            "classpath:database/books/insert_one_book.sql",
            "classpath:database/orders/insert_one_order.sql",
            "classpath:database/order_items/insert_one_order_item.sql",
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/clear_database.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void viewOrderItems_ValidOrderId_Success() throws Exception {
        //Given
        Long orderId = 1L;

        OrderItemDto expectedOrderItemDto = createDefaultOrderItemDtoSample();

        MvcResult result = mockMvc.perform(get("/orders/{orderId}/items", orderId))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        List<OrderItemDto> actualOrderItemDtosList = objectMapper.readValue(result.getResponse()
                .getContentAsString(), new TypeReference<>() {
                });

        assertNotNull(actualOrderItemDtosList);
        assertEquals(1, actualOrderItemDtosList.size());
        assertTrue(EqualsBuilder.reflectionEquals(expectedOrderItemDto,
                actualOrderItemDtosList.getFirst()));
    }

    @WithUserDetails(value = USER_EMAIL, userDetailsServiceBeanName = "customUserDetailsService")
    @Test
    @DisplayName("""
            viewOrderItems():
             Should return 404 NOT FOUND when given invalid order ID
            """)
    @Sql(scripts = {
            "classpath:database/users/insert_one_user.sql",
            "classpath:database/users_roles/set_user_one_roles_USER.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/clear_database.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void viewOrderItems_InvalidOrderId_NotFound() throws Exception {
        //Given
        Long orderId = 99L;

        //When & Then
        MvcResult result = mockMvc.perform(get("/orders/{orderId}/items", orderId))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @WithUserDetails(value = USER_EMAIL, userDetailsServiceBeanName = "customUserDetailsService")
    @Test
    @DisplayName("""
            viewOrderItem():
             Verifying retrieval of an order item by its ID and order ID
            """)
    @Sql(scripts = {
            "classpath:database/users/insert_one_user.sql",
            "classpath:database/users_roles/set_user_one_roles_USER.sql",
            "classpath:database/books/insert_one_book.sql",
            "classpath:database/orders/insert_one_order.sql",
            "classpath:database/order_items/insert_one_order_item.sql",
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/clear_database.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void viewOrderItem_ValidOrderAndItemIds_Success() throws Exception {
        //Given
        Long orderId = 1L;
        Long orderItemId = 1L;

        OrderItemDto expectedOrderItemDto = createDefaultOrderItemDtoSample();

        MvcResult result = mockMvc.perform(get("/orders/{orderId}/items/{orderItemId}",
                        orderId, orderItemId))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        OrderItemDto actualOrderItemDto = objectMapper.readValue(result.getResponse()
                .getContentAsString(), OrderItemDto.class);

        assertNotNull(actualOrderItemDto);
        assertTrue(EqualsBuilder.reflectionEquals(expectedOrderItemDto, actualOrderItemDto));
    }

    @WithUserDetails(value = USER_EMAIL, userDetailsServiceBeanName = "customUserDetailsService")
    @Test
    @DisplayName("""
            viewOrderItem():
             Should return 404 NOT FOUND when given invalid order ID
            """)
    @Sql(scripts = {
            "classpath:database/users/insert_one_user.sql",
            "classpath:database/users_roles/set_user_one_roles_USER.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/clear_database.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void viewOrderItem_InvalidOrderId_NotFound() throws Exception {
        //Given
        Long orderId = 99L;
        Long orderItemId = 1L;

        //When & Then
        MvcResult result = mockMvc.perform(get("/orders/{orderId}/items/{orderItemId}",
                        orderId, orderItemId))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @WithUserDetails(value = USER_EMAIL, userDetailsServiceBeanName = "customUserDetailsService")
    @Test
    @DisplayName("""
            viewOrderItem():
             Should return 404 NOT FOUND when given invalid order item ID
            """)
    @Sql(scripts = {
            "classpath:database/users/insert_one_user.sql",
            "classpath:database/users_roles/set_user_one_roles_USER.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/clear_database.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void viewOrderItem_InvalidOrderItemId_NotFound() throws Exception {
        //Given
        Long orderId = 1L;
        Long invalidOrderItemId = 99L;

        //When & Then
        MvcResult result = mockMvc.perform(get("/orders/{orderId}/items/{orderItemId}",
                        orderId, invalidOrderItemId))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @WithUserDetails(value = USER_EMAIL, userDetailsServiceBeanName = "customUserDetailsService")
    @Test
    @DisplayName("""
            placeOrder():
             Confirming successful creation of an order with valid request
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
    void placeOrder_ValidRequestDto_Success() throws Exception {
        //Given
        Long expectedUserId = 3L;
        BigDecimal expectedTotal = new BigDecimal("39.99");

        CreateOrderRequestDto requestDto = new CreateOrderRequestDto(ORDER_SHIPPING_ADDRESS);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        OrderDto expectedOrderDto = createDefaultEmptyOrderDtoSample();
        expectedOrderDto.setUserId(expectedUserId);
        expectedOrderDto.setTotal(expectedTotal);

        OrderItemDto expectedOrderItemDto = createDefaultOrderItemDtoSample();

        expectedOrderDto.setOrderItems(new ArrayList<>(List.of(expectedOrderItemDto)));

        Long expectedOrderId = 1L;

        //When
        MvcResult result = mockMvc.perform(
                post("/orders")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        //Then
        OrderDto actualOrderDto = objectMapper.readValue(result.getResponse()
                .getContentAsString(), OrderDto.class);

        assertNotNull(actualOrderDto);
        assertEquals(expectedOrderId, actualOrderDto.getId());
        assertEquals(expectedUserId, actualOrderDto.getUserId());
        assertEquals(1, actualOrderDto.getOrderItems().size());
        assertTrue(EqualsBuilder.reflectionEquals(expectedOrderDto,
                actualOrderDto, "orderItems", "orderDate"));
        assertTrue(EqualsBuilder.reflectionEquals(expectedOrderItemDto,
                actualOrderDto.getOrderItems().getFirst()));
    }

    @WithUserDetails(value = USER_EMAIL, userDetailsServiceBeanName = "customUserDetailsService")
    @Test
    @DisplayName("""
            placeOrder():
             Should return 400 BAD REQUEST when user have empty shopping cart
            """)
    @Sql(scripts = {
            "classpath:database/users/insert_one_user.sql",
            "classpath:database/users_roles/set_user_one_roles_USER.sql",
            "classpath:database/shopping_carts/insert_one_shopping_cart.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/clear_database.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void placeOrder_EmptyShoppingCart_BadRequest() throws Exception {
        //Given
        CreateOrderRequestDto requestDto = new CreateOrderRequestDto(ORDER_SHIPPING_ADDRESS);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When & Then
        MvcResult result = mockMvc.perform(
                        post("/orders")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @WithUserDetails(value = USER_EMAIL, userDetailsServiceBeanName = "customUserDetailsService")
    @Test
    @DisplayName("""
            placeOrder():
             Should return 400 BAD REQUEST when given invalid request body
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
    void placeOrder_InvalidRequestDto_BadRequest() throws Exception {
        //Given
        CreateOrderRequestDto requestDto = new CreateOrderRequestDto(null);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When & Then
        MvcResult result = mockMvc.perform(
                        post("/orders")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @WithUserDetails(value = USER_EMAIL, userDetailsServiceBeanName = "customUserDetailsService")
    @Test
    @DisplayName("""
            updateOrderStatus():
             Verifying updating order status by ID with valid request
            """)
    @Sql(scripts = {
            "classpath:database/users/insert_one_user.sql",
            "classpath:database/users_roles/set_user_one_roles_ADMIN.sql",
            "classpath:database/books/insert_one_book.sql",
            "classpath:database/orders/insert_one_order.sql",
            "classpath:database/order_items/insert_one_order_item.sql",
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/clear_database.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateOrderStatus_ValidRequestDtoAndOrderId_Success() throws Exception {
        //Given
        Long expectedOrderId = 1L;
        Long expectedUserId = 3L;
        String updatedOrderStatus = "DELIVERED";

        UpdateOrderStatusRequestDto requestDto = new UpdateOrderStatusRequestDto(
                updatedOrderStatus);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        OrderDto expectedOrderDto = createDefaultEmptyOrderDtoSample();
        expectedOrderDto.setUserId(expectedUserId);
        expectedOrderDto.setStatus(updatedOrderStatus);

        OrderItemDto expectedOrderItemDto = createDefaultOrderItemDtoSample();

        expectedOrderDto.setOrderItems(new ArrayList<>(List.of(expectedOrderItemDto)));

        MvcResult result = mockMvc.perform(
                        patch("/orders/{orderId}", expectedOrderId)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        OrderDto actualOrderDto = objectMapper.readValue(result.getResponse()
                .getContentAsString(), OrderDto.class);

        assertNotNull(actualOrderDto);
        assertEquals(expectedOrderId, actualOrderDto.getId());
        assertEquals(expectedUserId, actualOrderDto.getUserId());
        assertTrue(EqualsBuilder.reflectionEquals(expectedOrderDto,
                actualOrderDto, "orderItems", "orderDate"));
        assertTrue(EqualsBuilder.reflectionEquals(expectedOrderItemDto,
                actualOrderDto.getOrderItems().getFirst()));
    }

    @WithUserDetails(value = USER_EMAIL, userDetailsServiceBeanName = "customUserDetailsService")
    @Test
    @DisplayName("""
            updateOrderStatus():
             Should return 404 NOT FOUND when given invalid order ID
            """)
    @Sql(scripts = {
            "classpath:database/users/insert_one_user.sql",
            "classpath:database/users_roles/set_user_one_roles_ADMIN.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/clear_database.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateOrderStatus_InvalidOrderId_NotFound() throws Exception {
        //Given
        Long invalidOrderId = 99L;
        String updatedOrderStatus = "DELIVERED";

        UpdateOrderStatusRequestDto requestDto = new UpdateOrderStatusRequestDto(
                updatedOrderStatus);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When & Then
        MvcResult result = mockMvc.perform(
                        patch("/orders/{orderId}", invalidOrderId)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @WithUserDetails(value = USER_EMAIL, userDetailsServiceBeanName = "customUserDetailsService")
    @Test
    @DisplayName("""
            updateOrderStatus():
             Should return 400 BAD REQUEST when given invalid request body
            """)
    @Sql(scripts = {
            "classpath:database/users/insert_one_user.sql",
            "classpath:database/users_roles/set_user_one_roles_ADMIN.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/clear_database.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateOrderStatus_InvalidRequestDto_BadRequest() throws Exception {
        //Given
        Long orderId = 1L;

        UpdateOrderStatusRequestDto invalidRequestDto = new UpdateOrderStatusRequestDto(null);
        String jsonRequest = objectMapper.writeValueAsString(invalidRequestDto);

        //When & Then
        MvcResult result = mockMvc.perform(
                        patch("/orders/{orderId}", orderId)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @WithUserDetails(value = USER_EMAIL, userDetailsServiceBeanName = "customUserDetailsService")
    @Test
    @DisplayName("""
            updateOrderStatus():
             Should return 403 FORBIDDEN when user doesn't have authority 'ADMIN'
            """)
    @Sql(scripts = {
            "classpath:database/users/insert_one_user.sql",
            "classpath:database/users_roles/set_user_one_roles_USER.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/clear_database.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateOrderStatus_InvalidUserAuthority_Forbidden() throws Exception {
        //Given
        Long orderId = 1L;
        String updatedOrderStatus = "DELIVERED";

        UpdateOrderStatusRequestDto requestDto = new UpdateOrderStatusRequestDto(
                updatedOrderStatus);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When & Then
        MvcResult result = mockMvc.perform(
                        patch("/orders/{orderId}", orderId)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();
    }
}
