package mate.academy.intro.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.ISBN;

@Getter
@Setter
public class CreateBookRequestDto {
    @NotNull
    private String title;

    @NotNull
    private String author;

    @NotNull
    @ISBN(type = ISBN.Type.ISBN_13)
    private String isbn;

    @NotNull
    @Min(0)
    private BigDecimal price;

    private String description;
    private String coverImage;
}
