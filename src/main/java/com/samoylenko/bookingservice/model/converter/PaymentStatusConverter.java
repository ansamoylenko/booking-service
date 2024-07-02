package com.samoylenko.bookingservice.model.converter;

import com.samoylenko.bookingservice.model.payment.PaymentStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PaymentStatusConverter implements Converter<String, PaymentStatus> {
    @Override
    public PaymentStatus convert(String source) {
        return PaymentStatus.fromDescription(source);
    }
}
