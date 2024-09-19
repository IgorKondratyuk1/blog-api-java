package org.development.blogApi.common.validation.loginOrEmailNotUsed;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.development.blogApi.common.validation.loginOrEmailNotUsed.LoginOrEmailNotUsed;
import org.development.blogApi.user.entity.UserEntity;
import org.development.blogApi.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class LoginOrEmailNotUsedValidator implements ConstraintValidator<LoginOrEmailNotUsed, String> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void initialize(LoginOrEmailNotUsed emailNotUsed) {
        ConstraintValidator.super.initialize(emailNotUsed);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        Optional<UserEntity> userEntityOptional = userRepository.findByLoginOrEmail(value);
        return userEntityOptional.isEmpty();
    }
}
