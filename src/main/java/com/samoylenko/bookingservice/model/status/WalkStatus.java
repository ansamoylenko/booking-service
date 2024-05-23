package com.samoylenko.bookingservice.model.status;

import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;

@Getter
@Generated
@AllArgsConstructor
public enum WalkStatus {
    DRAFT("Черновик прогулки"),
    BOOKING_IN_PROGRESS("Запись активна"),
    BOOKING_PAUSED("Запись приостановлена"),
    BOOKING_FINISHED("Запись завершена"),
    FINISHED("Прогулка завершена"),
    CANCELED("Прогулка отменена"),
    DELETED("Прогулка удалена");

    private final String description;
}
