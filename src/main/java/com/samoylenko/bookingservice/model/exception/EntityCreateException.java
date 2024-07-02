package com.samoylenko.bookingservice.model.exception;

public class EntityCreateException extends RuntimeException {
    private final EntityType entityType;

    public EntityCreateException(EntityType entityType, Throwable cause) {
        super(String.format("Failed to create %s", entityType), cause);
        this.entityType = entityType;
    }
}
