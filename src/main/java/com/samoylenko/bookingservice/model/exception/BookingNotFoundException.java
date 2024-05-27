package com.samoylenko.bookingservice.model.exception;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(String bookingId) {
        super(bookingId);
    }
}
