package com.samoylenko.bookingservice.model.converter;

import com.samoylenko.bookingservice.model.walk.WalkStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class WalkStatusConverter implements Converter<String, WalkStatus> {
    @Override
    public WalkStatus convert(String source) {
        return WalkStatus.fromDescription(source);
    }
}
