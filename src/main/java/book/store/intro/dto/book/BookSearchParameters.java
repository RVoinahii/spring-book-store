package book.store.intro.dto.book;

import java.math.BigDecimal;

public record BookSearchParameters(
        String title,
        String author,
        String isbn,
        BigDecimal bottomPrice,
        BigDecimal upperPrice) {
}
