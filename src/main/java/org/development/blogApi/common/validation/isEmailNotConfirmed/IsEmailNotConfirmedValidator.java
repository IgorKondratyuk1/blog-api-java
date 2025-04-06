package org.development.blogApi.common.validation.isEmailNotConfirmed;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.development.blogApi.modules.user.entity.UserEntity;
import org.development.blogApi.modules.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class IsEmailNotConfirmedValidator implements ConstraintValidator<IsEmailNotConfirmed, String> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void initialize(IsEmailNotConfirmed emailNotUsed) {
        ConstraintValidator.super.initialize(emailNotUsed);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        Optional<UserEntity> userEntityOptional = userRepository.findByLoginOrEmail(value);
        if (userEntityOptional.isEmpty()) {
            return false;
        }

        return !userEntityOptional.get().getEmailConfirmation().isConfirmed();
    }
}
