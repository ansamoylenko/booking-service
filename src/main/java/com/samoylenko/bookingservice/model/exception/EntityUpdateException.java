package com.samoylenko.bookingservice.model.exception;

public class EntityUpdateException extends RuntimeException {
    private final EntityType entityType;

    public EntityUpdateException(EntityType entityType, Throwable cause) {
        super(String.format("Failed to update %s", entityType), cause);
        this.entityType = entityType;
    }
}
