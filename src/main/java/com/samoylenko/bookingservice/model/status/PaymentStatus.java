package com.samoylenko.bookingservice.model.status;

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
    private String description;
}
