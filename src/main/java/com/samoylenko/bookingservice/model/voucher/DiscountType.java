package com.samoylenko.bookingservice.model.voucher;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.ToString;

@ToString
@Generated
@AllArgsConstructor
public enum DiscountType {
    NONE("Без скидки"),
    PROMO_CODE("Промокод"),
    CERTIFICATE("Сертификат"),
    REPEATED_BOOKING("Повторное бронирование"),
    GROUP_BOOKING("Групповое бронирование");

    private final String description;

    @JsonValue
    public String getDescription() {
        return description;
    }
}
