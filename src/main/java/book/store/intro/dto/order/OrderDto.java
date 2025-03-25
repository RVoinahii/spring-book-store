package book.store.intro.dto.order;

import book.store.intro.dto.order.item.OrderItemDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderDto {
    private Long id;
    private Long userId;
    private List<OrderItemDto> orderItems;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime orderDate;

    private BigDecimal total;
    private String status;
}
