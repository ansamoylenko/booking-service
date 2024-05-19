package com.samoylenko.bookingservice.service;

import com.samoylenko.bookingservice.model.dto.PaymentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    public Page<PaymentDto> getPayments(PageRequest pageRequest) {
        return null;
    }

    public PaymentDto getPaymentById(String id) {
        return null;
    }
}
