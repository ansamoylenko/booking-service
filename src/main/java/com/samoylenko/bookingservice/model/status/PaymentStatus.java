package com.samoylenko.bookingservice.model.status;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.ToString;

@ToString
@Generated
@AllArgsConstructor
public enum PaymentStatus {
    PENDING("Ожидание оплаты"),
    PAID("Успешная оплата"),
    REFUNDED("Оплата отменена"),
    CANCELED("Оплата не прошла");

    private final String description;

    @JsonValue
    public String getDescription() {
        return description;
    }

    public static PaymentStatus fromDescription(String description) {
        if (description == null) {
            return null;
        }
        for (PaymentStatus status : PaymentStatus.values()) {
            if (status.getDescription().equals(description)) {
                return status;
            }
        }
        return null;
    }
}
