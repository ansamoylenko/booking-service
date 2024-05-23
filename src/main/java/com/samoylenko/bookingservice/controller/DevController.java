package com.samoylenko.bookingservice.controller;

import com.samoylenko.bookingservice.repository.RouteRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dev")
@AllArgsConstructor
public class DevController {
    private final RouteRepository routeRepository;

    @DeleteMapping("/routes")
    public void deleteAllRoutes() {
        routeRepository.deleteAll();
    }
}
