package com.samoylenko.bookingservice.model.exception;

public class EntityNotFoundException extends RuntimeException {
    private final EntityType entityType;
    private final String entityId;

    public EntityNotFoundException(EntityType entityType, String entityId) {
        super(String.format("%s with id %s was not found", entityType, entityId));
        this.entityType = entityType;
        this.entityId = entityId;
    }
}
