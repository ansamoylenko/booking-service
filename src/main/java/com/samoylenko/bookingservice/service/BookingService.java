package com.samoylenko.bookingservice.service;

import com.samoylenko.bookingservice.model.dto.BookingDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class BookingService {
    public BookingDto createOrder(BookingDto record) {
        return null;
    }

    public Page<BookingDto> getOrders(PageRequest pageRequest) {
        return null;
    }

    public BookingDto getOrderById(String id) {
        return null;
    }

    public void deleteOrder(String id) {
    }

    public BookingDto updateOrder(String id, BookingDto record) {
        return null;
    }
}
