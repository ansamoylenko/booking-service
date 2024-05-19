package com.samoylenko.bookingservice.model.status;

import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.ToString;

@ToString
@Generated
@AllArgsConstructor
public enum OrderStatus {
    DRAFT("Создан черновик заявки"),
    PAID("Заявка оплачена"),
    CANCELED("Заявка отменена"),
    REJECTED("Заявка отклонена"),
    COMPLETED("Заявка выполнена");

    private final String description;
}
