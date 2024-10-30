package org.development.blogApi.infrastructure.common.validation.enumCheck;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.development.blogApi.infrastructure.common.enums.ValueEnum;

import java.util.Arrays;

public class EnumCheckValidator implements ConstraintValidator<EnumCheck, String> {
    private Class<? extends Enum<?>> enumClass;

    @Override
    public void initialize(EnumCheck annotation) {
        this.enumClass = annotation.enumClass();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        return Arrays.stream(enumClass.getEnumConstants())
                .anyMatch(e -> ((ValueEnum<?>) e).getValue().equals(value));
    }
}
