package com.samoylenko.bookingservice.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TitleValidator implements ConstraintValidator<Title, String> {
    private boolean nullable;

    @Override
    public void initialize(Title constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.nullable = constraintAnnotation.nullable();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (nullable && value == null) {
            return true;
        }
        return value != null && value.matches("[a-zA-Zа-яА-Я ]+");
    }
}
