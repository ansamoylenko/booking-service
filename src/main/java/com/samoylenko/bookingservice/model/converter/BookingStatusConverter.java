package com.samoylenko.bookingservice.model.converter;

import com.samoylenko.bookingservice.model.booking.BookingStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class BookingStatusConverter implements Converter<String, BookingStatus> {
    @Override
    public BookingStatus convert(String source) {
        return BookingStatus.fromDescription(source);
    }
}
