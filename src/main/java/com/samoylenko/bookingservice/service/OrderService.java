package com.samoylenko.bookingservice.service;

import com.samoylenko.bookingservice.model.dto.OrderDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    public OrderDto createOrder(OrderDto OrderDto) {
        return null;
    }

    public Page<OrderDto> getOrders(PageRequest pageRequest) {
        return null;
    }

    public OrderDto getOrderById(String id) {
        return null;
    }

    public void deleteOrder(String id) {
    }

    public OrderDto updateOrder(String id, OrderDto OrderDto) {
        return null;
    }
}
