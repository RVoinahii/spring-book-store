package mate.academy.intro.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequestDto(
        @NotBlank
        String name,

        @Size(max = 255)
        String description
) {
}
