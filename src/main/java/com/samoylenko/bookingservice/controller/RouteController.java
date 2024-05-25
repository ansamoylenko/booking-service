package com.samoylenko.bookingservice.controller;

import com.samoylenko.bookingservice.model.dto.request.WalkRequest;
import com.samoylenko.bookingservice.model.dto.route.RouteCreateDto;
import com.samoylenko.bookingservice.model.dto.route.RouteDto;
import com.samoylenko.bookingservice.model.dto.route.RouteUpdateDto;
import com.samoylenko.bookingservice.model.dto.walk.WalkDto;
import com.samoylenko.bookingservice.service.RouteService;
import com.samoylenko.bookingservice.service.WalkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
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
    private final WalkService walkService;

    @Operation(summary = "Добавить новый маршрут")
    @PostMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<RouteDto> createRouteDto(@RequestBody RouteCreateDto route, UriComponentsBuilder uriBuilder) {
        var created = routeService.createRoute(route);
        var location = uriBuilder.path("/api/v1/routes/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
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
    public RouteDto updateRouteDto(@PathVariable String id, @RequestBody RouteUpdateDto route) {
        return routeService.updateRoute(id, route);
    }

    @Operation(summary = "Пометить маршрут как удаленный")
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRouteDto(@PathVariable String id) {
        routeService.markDeleted(id);
    }

    @Operation(summary = "Получить прогулки по маршруту")
    @GetMapping(value = "/{id}/walks", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Page<WalkDto> getWalks(@PathVariable String id) {
        var request = WalkRequest.builder()
                .routeId(id)
                .build();
        return walkService.getWalksForUser(request);
    }
}
