package com.samoylenko.bookingservice.model.status.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.samoylenko.bookingservice.model.status.VoucherStatus;
import lombok.AllArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class VoucherStatusToStringConverter implements Converter<VoucherStatus, String> {
    private final ObjectMapper objectMapper;

    @Override
    public String convert(VoucherStatus source) {
        try {
            return objectMapper.writeValueAsString(source);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}