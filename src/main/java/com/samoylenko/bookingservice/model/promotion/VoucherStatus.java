package com.samoylenko.bookingservice.model.promotion;

import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;

@Getter
@Generated
@AllArgsConstructor
public enum VoucherStatus {
    ACTIVE("Активный", true),
    EXPIRED("Истекший", false),
    APPLIED("Применен", false);

    public final String message;
    public final boolean isValid;
}

