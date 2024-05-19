package com.samoylenko.bookingservice.model.status;

import lombok.AllArgsConstructor;
import lombok.Getter;

//@ToString
@Getter
@AllArgsConstructor
public enum WalkStatus {
    DRAFT("Черновик прогулки"),
    RECORDING_IN_PROGRESS("Запись активна"),
    RECORDING_PAUSED("Запись приостановлена"),
    RECORDING_FINISHED("Запись завершена"),
    FINISHED("Прогулка завершена"),
    CANCELED("Прогулка отменена");

    private final String description;
}
