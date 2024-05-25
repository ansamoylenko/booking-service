package com.samoylenko.bookingservice.controller;

import com.samoylenko.bookingservice.model.dto.request.WalkRequest;
import com.samoylenko.bookingservice.model.dto.walk.CompositeUserWalkDto;
import com.samoylenko.bookingservice.model.dto.walk.WalkDto;
import com.samoylenko.bookingservice.service.WalkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/v1/walks")
@Tag(name = "Прогулки")
@AllArgsConstructor
public class WalkController {
    private final WalkService walkService;

    @Operation(summary = "Получить прогулку по id")
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public CompositeUserWalkDto getWalkForUser(@PathVariable String id) {
        return walkService.getWalkForUser(id);
    }

    @Operation(summary = "Получить страницу с прогулками", description = "Допустима фильтрация по маршруту и количеству доступных мест")
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Page<WalkDto> getPageOfWalks(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "routeId", required = false) String routeId,
            @RequestParam(value = "places", defaultValue = "1") Integer places
    ) {
        var request = WalkRequest.builder()
                .pageNumber(page)
                .pageSize(size)
                .routeId(routeId)
                .availablePlaces(places)
                .build();
        return walkService.getWalksForUser(request);
    }
}
