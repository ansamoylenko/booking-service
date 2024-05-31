package com.samoylenko.bookingservice.model.exception;

public class VoucherNotFoundException extends RuntimeException {
    public VoucherNotFoundException(String id) {
        super(id);
    }
}
