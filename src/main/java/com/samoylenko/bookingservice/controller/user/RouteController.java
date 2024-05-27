package com.samoylenko.bookingservice.controller.user;

import com.samoylenko.bookingservice.model.dto.route.RouteDto;
import com.samoylenko.bookingservice.service.RouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/v1/routes")
@Tag(name = "Маршруты (для пользователей)")
@AllArgsConstructor
public class RouteController {
    private final RouteService routeService;

    @Operation(summary = "Получить все маршруты")
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<RouteDto> getAllRouteDtos() {
        return routeService.getAllRoutes();
    }

    @Operation(summary = "Получить маршрут по id")
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public RouteDto getRouteDto(@PathVariable String id) {
        return routeService.getRouteById(id);
    }
}
