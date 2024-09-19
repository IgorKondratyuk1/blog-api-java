package org.development.blogApi.common.validation.codeNotUsed;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.development.blogApi.user.entity.UserEntity;
import org.development.blogApi.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.UUID;

public class CodeNotUsedValidator implements ConstraintValidator<CodeNotUsed, String> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void initialize(CodeNotUsed emailNotUsed) {
        ConstraintValidator.super.initialize(emailNotUsed);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        Optional<UserEntity> userEntityOptional = userRepository.findByEmailConfirmationCode(UUID.fromString(value));
        if (userEntityOptional.isEmpty()) {
            return false;
        }

        return !userEntityOptional.get().getEmailConfirmation().isConfirmed();
    }
}
