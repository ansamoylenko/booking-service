package com.samoylenko.bookingservice.controller.user;

import com.samoylenko.bookingservice.model.walk.CompositeUserWalkDto;
import com.samoylenko.bookingservice.model.walk.WalkDto;
import com.samoylenko.bookingservice.model.walk.WalkRequest;
import com.samoylenko.bookingservice.model.walk.WalkStatus;
import com.samoylenko.bookingservice.service.WalkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/v1/walks")
@Tag(name = "Прогулки (для пользователя)")
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
            @RequestParam(value = "places", defaultValue = "1") Integer places,
            @RequestParam(value = "sortBy", required = false, defaultValue = "START_TIME") WalkRequest.SortField sortBy,
            @RequestParam(value = "direction", required = false, defaultValue = "DESC") Sort.Direction direction
    ) {
        var request = WalkRequest.builder()
                .pageNumber(page)
                .pageSize(size)
                .status(WalkStatus.BOOKING_IN_PROGRESS)
                .routeId(routeId)
                .availablePlaces(places)
                .sortBy(sortBy)
                .direction(direction)
                .build();
        return walkService.getWalksForUser(request);
    }
}
