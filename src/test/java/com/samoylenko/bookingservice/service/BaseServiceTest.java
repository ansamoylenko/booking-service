package com.samoylenko.bookingservice.service;

import com.samoylenko.bookingservice.repository.*;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@AllArgsConstructor
public class BaseServiceTest {
    final WalkRepository walkRepository;
    final RouteRepository routeRepository;
    final EmployeeRepository employeeRepository;
    final BookingRepository bookingRepository;
    final ClientRepository clientRepository;
    final PaymentRepository paymentRepository;

    @BeforeEach
    public void setUp() {
        paymentRepository.deleteAll();
        walkRepository.deleteAll();
        routeRepository.deleteAll();
        bookingRepository.deleteAll();
        employeeRepository.deleteAll();
        clientRepository.deleteAll();
    }

    @AfterEach
    public void tearDown() {
        paymentRepository.deleteAll();
        walkRepository.deleteAll();
        routeRepository.deleteAll();
        employeeRepository.deleteAll();
        bookingRepository.deleteAll();
        clientRepository.deleteAll();
    }
}
