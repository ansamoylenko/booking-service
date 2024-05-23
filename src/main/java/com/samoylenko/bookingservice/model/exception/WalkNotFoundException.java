package com.samoylenko.bookingservice.model.exception;

public class WalkNotFoundException extends RuntimeException {
        public WalkNotFoundException(String walkId) {
                super("walk with id %s not found".formatted(walkId));
        }
}


