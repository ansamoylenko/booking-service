package com.samoylenko.bookingservice.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneValidator.class)
public @interface Phone {
    String pattern() default "^(7)[0-9]{10}";

    String message() default "Incorrect format. Correct: 77777777777";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
