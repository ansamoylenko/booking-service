package com.samoylenko.bookingservice.model.exception;

public class RouteNotFoundException extends RuntimeException {
    public RouteNotFoundException(String routeId) {
        super("route with id %s not found".formatted(routeId));
    }
}
