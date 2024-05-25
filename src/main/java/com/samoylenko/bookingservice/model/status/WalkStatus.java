package com.samoylenko.bookingservice.model.status;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;

@Getter
@Generated
@AllArgsConstructor
public enum WalkStatus {
    DRAFT("Черновик"),
    BOOKING_IN_PROGRESS("Запись активна"),
    BOOKING_PAUSED("Запись приостановлена"),
    BOOKING_FINISHED("Запись завершена"),
    FINISHED("Прогулка завершена"),
    CANCELED("Прогулка отменена"),
    DELETED("Прогулка удалена");

    private final String description;

    @JsonValue
    public String getDescription() {
        return description;
    }

    public static WalkStatus fromDescription(String description) {
        if (description == null) {
            return null;
        }
        for (WalkStatus status : WalkStatus.values()) {
            if (status.getDescription().equals(description)) {
                return status;
            }
        }
        return null;
    }
}
