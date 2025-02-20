package mate.academy.intro.dto.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import mate.academy.intro.dto.order.item.OrderItemDto;

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
