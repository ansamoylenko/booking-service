package com.samoylenko.bookingservice.model.status;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.ToString;

@ToString
@Generated
@AllArgsConstructor
public enum BookingStatus {
    DRAFT("Создан черновик заявки"),
    WAITING_FOR_PAYMENT("Ожидание оплаты"),
    PAID("Заявка оплачена"),
    CANCELED("Заявка отменена"),
    REJECTED("Заявка отклонена"),
    COMPLETED("Заявка выполнена");

    private final String description;

    @JsonValue
    public String getDescription() {
        return description;
    }

    public static BookingStatus fromDescription(String description) {
        if (description == null) {
            return null;
        }
        for (BookingStatus status : BookingStatus.values()) {
            if (status.getDescription().equals(description)) {
                return status;
            }
        }
        return null;
    }
}
