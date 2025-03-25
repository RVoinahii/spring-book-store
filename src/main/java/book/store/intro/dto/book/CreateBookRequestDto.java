package book.store.intro.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.ISBN;

@Getter
@Setter
public class CreateBookRequestDto {
    @NotBlank
    private String title;

    @NotBlank
    private String author;

    @NotBlank
    @ISBN(type = ISBN.Type.ISBN_13)
    private String isbn;

    @NotNull
    @Positive
    private BigDecimal price;

    private String description;

    @NotEmpty
    private List<Long> categories;

    private String coverImage;
}
