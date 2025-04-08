package org.development.blogApi.common.validation.isEmailNotConfirmed;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IsEmailNotConfirmedValidator.class)
@Documented()
public @interface IsEmailNotConfirmed {
    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default  {};
}
