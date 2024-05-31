package com.samoylenko.bookingservice.model.voucher;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.ToString;

@ToString
@Generated
@AllArgsConstructor
public enum VoucherType {
    PROMO_CODE("Промокод"),
    CERTIFICATE("Сертификат");

    private final String description;

    @JsonValue
    public String getDescription() {
        return description;
    }
}
