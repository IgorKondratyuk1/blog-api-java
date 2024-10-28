package org.development.blogApi.infrastructure.common.validation.codeNotUsed;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CodeNotUsedValidator.class)
@Documented()
public @interface CodeNotUsed {
    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default  {};
}
