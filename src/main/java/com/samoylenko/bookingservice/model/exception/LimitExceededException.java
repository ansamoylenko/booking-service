package com.samoylenko.bookingservice.model.exception;

public class LimitExceededException extends RuntimeException {
    public LimitExceededException(String resourceId) {
        super("Limit exited for walk " + resourceId);
    }
}
