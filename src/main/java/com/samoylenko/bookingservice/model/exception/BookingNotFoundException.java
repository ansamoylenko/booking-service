package com.samoylenko.bookingservice.model.exception;

import lombok.Getter;

@Getter
public class BookingNotFoundException extends RuntimeException {
    private final String entityId;
    public BookingNotFoundException(String bookingId) {
        super("Booking with if %s not found");
        this.entityId = bookingId;
    }
}
