package com.samoylenko.bookingservice.model.status;

import lombok.AllArgsConstructor;
import lombok.Generated;

@Generated
@AllArgsConstructor
public enum VoucherStatus {
    ACTIVE("Активный", true),
    EXPIRED("Истекший", false),
    APPLIED("Применен", true);

    public final String message;
    public final boolean isValid;
}

