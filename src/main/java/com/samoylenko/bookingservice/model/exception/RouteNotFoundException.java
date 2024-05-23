package com.samoylenko.bookingservice.model.exception;

public class RouteNotFoundException extends RuntimeException {
    public RouteNotFoundException(String routeId) {
        super(routeId);
    }
}
