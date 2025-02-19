package mate.academy.intro.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.stream.Collectors;

public class OrderStatusValidator implements ConstraintValidator<OrderStatus, String> {
    private Enum<?>[] enumValues;

    @Override
    public void initialize(OrderStatus annotation) {
        enumValues = annotation.enumClass().getEnumConstants();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        boolean valid = Arrays.stream(enumValues)
                .map(Enum::name)
                .anyMatch(enumValue -> enumValue.equals(value));

        if (!valid) {
            String validEnumValues = Arrays.stream(enumValues)
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));

            context.buildConstraintViolationWithTemplate(
                    "Invalid order status. Must contain one of: " + validEnumValues)
                    .addConstraintViolation();
        }
        return valid;
    }
}
