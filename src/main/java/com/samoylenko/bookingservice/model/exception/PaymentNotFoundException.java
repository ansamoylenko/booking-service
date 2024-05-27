package com.samoylenko.bookingservice.model.exception;

public class PaymentNotFoundException extends RuntimeException {
    public PaymentNotFoundException(String paymentId) {
        super(paymentId);
    }
}
