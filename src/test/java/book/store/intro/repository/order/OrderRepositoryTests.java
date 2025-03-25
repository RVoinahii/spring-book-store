package book.store.intro.repository.order;

import static book.store.intro.util.TestBookDataUtil.PAGE_NUMBER;
import static book.store.intro.util.TestBookDataUtil.PAGE_SIZE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import book.store.intro.model.Order;
import book.store.intro.model.OrderItem;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderRepositoryTests {
    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("""
            findAllByUserId():
             Should return a list with orders when a valid user ID is provided
            """)
    @Sql(scripts = {
            "classpath:database/users/insert_one_user.sql",
            "classpath:database/orders/insert_one_order.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/clear_database.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByUserId_ValidUserId_ReturnsNonEmptyList() {
        //Given
        Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        Long userId = 3L;

        //When
        Page<Order> actualOrderPage = orderRepository.findAllByUserId(pageable, userId);

        //Then
        assertEquals(1, actualOrderPage.getContent().size(),
                "Expected exactly one order to be returned for a valid user ID");
        assertEquals(1, actualOrderPage.getTotalElements(),
                "Total elements should be exactly 1");
        assertEquals(userId, actualOrderPage.getContent().getFirst().getUser().getId(),
                "Returned order should have the expected user ID");
        assertEquals(PAGE_NUMBER, actualOrderPage.getNumber(),
                "Page number should match the requested page");
        assertEquals(PAGE_SIZE, actualOrderPage.getSize(),
                "Page size should match the requested size");
    }

    @Test
    @DisplayName("""
            findAllByUserId():
             Should return an empty list when an invalid user ID is provided
            """)
    void findAllByUserId_InvalidUserId_ReturnsEmptyList() {
        //Given
        Long invalidUserId = 99L;

        Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

        //When
        Page<Order> actualOrderPage = orderRepository.findAllByUserId(pageable, invalidUserId);

        //Then
        assertTrue(actualOrderPage.getContent().isEmpty(),
                "Expected empty result for invalid user ID");
        assertEquals(0, actualOrderPage.getTotalElements(),
                "Total elements should be exactly 1");
        assertEquals(PAGE_NUMBER, actualOrderPage.getNumber(),
                "Page number should match the requested page");
        assertEquals(PAGE_SIZE, actualOrderPage.getSize(),
                "Page size should match the requested size");
    }

    @Test
    @DisplayName("""
            findByIdWithOrderItems():
             Should return order with order items list when a valid order ID is provided
            """)
    @Sql(scripts = {
            "classpath:database/users/insert_one_user.sql",
            "classpath:database/orders/insert_one_order.sql",
            "classpath:database/books/insert_one_book.sql",
            "classpath:database/order_items/insert_one_order_item.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/clear_database.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByIdWithOrderItems_ValidOrderId_ReturnsOrder() {
        //Given
        Long orderId = 1L;

        //When
        Optional<Order> actualOrder = orderRepository.findByIdWithOrderItems(orderId);

        //Then
        assertTrue(actualOrder.isPresent(), "Order must be present");
        assertEquals(orderId, actualOrder.get().getId());
        assertFalse(actualOrder.get().getOrderItems().isEmpty(), "Order items should not be empty");

        Long expectedOrderItemId = 1L;
        OrderItem actualOrderItem = actualOrder.get().getOrderItems().iterator().next();

        assertEquals(expectedOrderItemId, actualOrderItem.getId(),
                "Actual and expected must be equal");
    }

    @Test
    @DisplayName("""
            findByIdWithOrderItems():
             Should return an empty optional when an invalid order ID is provided
            """)
    void findByIdWithOrderItems_InvalidOrderId_ReturnsOptionalEmpty() {
        //Given
        Long invalidOrderId = 99L;

        //When
        Optional<Order> actualOrder = orderRepository.findByIdWithOrderItems(invalidOrderId);

        //Then
        assertTrue(actualOrder.isEmpty(), "Optional must be empty");
    }
}
