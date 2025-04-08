package org.development.blogApi.common.validation.loginOrEmailNotUsed;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LoginOrEmailNotUsedValidator.class)
@Documented()
public @interface LoginOrEmailNotUsed {
    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default  {};
}
