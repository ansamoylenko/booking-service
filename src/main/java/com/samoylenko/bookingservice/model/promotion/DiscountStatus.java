package com.samoylenko.bookingservice.model.promotion;

import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;

@Getter
@Generated
@AllArgsConstructor
public enum DiscountStatus {
    NONE("Нет", true),
    ACTIVE("Активный", true),
    EXPIRED("Истекший", false),
    ALREADY_APPlIED("Eже применен", false),
    NOT_APPLIED("Не применен", false);

    public final String message;
    public final boolean isValid;
}
