package com.samoylenko.bookingservice.service;

import com.samoylenko.bookingservice.repository.*;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

@AllArgsConstructor
public class BaseServiceTest {
    final WalkRepository walkRepository;
    final RouteRepository routeRepository;
    final EmployeeRepository employeeRepository;
    final BookingRepository bookingRepository;
    final ContactRepository contactRepository;
    final PaymentRepository paymentRepository;

    @BeforeEach
    public void setUp() {
        walkRepository.deleteAll();
        routeRepository.deleteAll();
        bookingRepository.deleteAll();
        employeeRepository.deleteAll();
        contactRepository.deleteAll();
        paymentRepository.deleteAll();
    }

    @AfterEach
    public void tearDown() {
        walkRepository.deleteAll();
        routeRepository.deleteAll();
        employeeRepository.deleteAll();
        bookingRepository.deleteAll();
        contactRepository.deleteAll();
        paymentRepository.deleteAll();
    }
}
