package com.samoylenko.bookingservice.controller;

import com.samoylenko.bookingservice.repository.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile({"local", "dev"})
@RestController()
@RequestMapping("/api/v1/dev")
@Tag(name = "Для разработчиков")
@AllArgsConstructor
public class DevController {
    final WalkRepository walkRepository;
    final RouteRepository routeRepository;
    final EmployeeRepository employeeRepository;
    final BookingRepository bookingRepository;
    final ClientRepository clientRepository;
    final PaymentRepository paymentRepository;

    @DeleteMapping("/all")
    public void deleteAll() {
        walkRepository.deleteAll();
        routeRepository.deleteAll();
        bookingRepository.deleteAll();
        employeeRepository.deleteAll();
        clientRepository.deleteAll();
        paymentRepository.deleteAll();
    }

    @DeleteMapping("/routes")
    public void deleteAllRoutes() {
        routeRepository.deleteAll();
    }

    @DeleteMapping("/routes/{id}")
    public void deleteRouteById(@PathVariable String id) {
        routeRepository.deleteById(id);
    }

}
