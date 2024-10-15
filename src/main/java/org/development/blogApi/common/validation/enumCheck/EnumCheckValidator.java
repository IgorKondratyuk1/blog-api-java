package org.development.blogApi.common.validation.enumCheck;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.development.blogApi.common.utils.UuidHelper;
import org.development.blogApi.core.like.enums.ValueEnum;
import org.development.blogApi.user.entity.UserEntity;
import org.development.blogApi.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

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

        Arrays.stream(enumClass.getEnumConstants()).forEach(e -> {
            System.out.println(((ValueEnum<?>) e).getValue());
        });


        return Arrays.stream(enumClass.getEnumConstants())
                .anyMatch(e -> ((ValueEnum<?>) e).getValue().equals(value));
    }
}
