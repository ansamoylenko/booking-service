package com.samoylenko.bookingservice.controller;

import com.samoylenko.bookingservice.model.dto.RouteDto;
import com.samoylenko.bookingservice.model.dto.WalkDto;
import com.samoylenko.bookingservice.service.RouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/v1/routes")
@Tag(name = "Маршруты")
@AllArgsConstructor
public class RouteController {
    private final RouteService routeService;

    @Operation(summary = "Добавить новый маршрут")
    @PostMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createRouteDto(@RequestBody RouteDto route, UriComponentsBuilder uriBuilder) {
        var created = routeService.createRoute(route);
        var location = uriBuilder.path("/api/v1/routes/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

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

    @Operation(summary = "Обновить параметры маршрута")
    @PatchMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public RouteDto updateRouteDto(@PathVariable String id, @RequestBody RouteDto route) {
        return routeService.updateRoute(id, route);
    }

    @Operation(summary = "Пометить маршрут как удаленный")
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRouteDto(@PathVariable String id) {
        routeService.deleteRoute(id);
    }

    @Operation(summary = "Получить прогулки по маршруту")
    @GetMapping(value = "/{id}/walks", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Page<WalkDto> getWalks(@PathVariable String id, PageRequest pageRequest) {
        return routeService.getWalksByRoute(id, pageRequest);
    }
}
