package com.samoylenko.bookingservice.controller;

import com.samoylenko.bookingservice.model.dto.request.WalkRequest;
import com.samoylenko.bookingservice.model.dto.walk.CompositeAdminWalkDto;
import com.samoylenko.bookingservice.model.dto.walk.WalkCreateDto;
import com.samoylenko.bookingservice.model.dto.walk.WalkDto;
import com.samoylenko.bookingservice.model.dto.walk.WalkUpdateDto;
import com.samoylenko.bookingservice.model.status.WalkStatus;
import com.samoylenko.bookingservice.service.WalkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/v1/admin/walks")
@Tag(name = "Прогулки. Админка")
@AllArgsConstructor
public class AdminWalkController {
    private final WalkService walkService;

    @Operation(summary = "Добавить новую прогулку")
    @PostMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<CompositeAdminWalkDto> addWalk(@RequestBody WalkCreateDto walk, UriComponentsBuilder uriBuilder) {
        var created = walkService.createWalk(walk);
        var location = uriBuilder.path("/api/v1/admin/walks/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Получить страницу с прогулками")
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Page<WalkDto> getPageOfWalks(
            @Schema(description = "Идентификатор маршрута", example = "3f5d6702-8554-4137-85e0-4ada615e7253")
            @RequestParam(value = "routeId", required = false) String routeId,
            @Schema(description = "Минимальное время начала прогулки", example = "2024-01-01T00:00:00")
            @RequestParam(value = "startAfter", required = false) LocalDateTime startAfter,
            @Schema(description = "Максимальное время начала прогулки", example = "2024-12-01T00:00:00")
            @RequestParam(value = "startBefore", required = false) LocalDateTime startBefore,
            @Schema(description = "Количество доступных мест", example = "2")
            @RequestParam(value = "availablePlaces", required = false) Integer availablePlaces,
            @Schema(description = "Номер страницы", example = "0")
            @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @Schema(description = "Размер страницы", example = "10")
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Schema(description = "Идентификатор сотрудника", example = "3f5d6702-8554-4137-85e0-4ada615e7253")
            @RequestParam(value = "employeeId", required = false) String employeeId,
            @Schema(description = "Статус прогулки", example = "Запись активна")
            @RequestParam(value = "status", required = false) String status
    ) {
        var request = WalkRequest.builder()
                .routeId(routeId)
                .startAfter(startAfter)
                .startBefore(startBefore)
                .availablePlaces(availablePlaces)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .employeeId(employeeId)
                .status(WalkStatus.fromDescription(status))
                .build();

        return walkService.getWalksForAdmin(request);
    }

    @Operation(summary = "Получить прогулку по id")
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public CompositeAdminWalkDto getWalk(@PathVariable String id) {
        return walkService.getWalkForAdmin(id);
    }

    @Operation(summary = "Обновить параметры прогулки")
    @PatchMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public CompositeAdminWalkDto updateWalk(@PathVariable String id, @RequestBody WalkUpdateDto walk) {
        return walkService.updateWalk(id, walk);
    }

    @Operation(summary = "Пометить прогулку как удаленную")
    @DeleteMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWalk(@PathVariable String id) {
        walkService.markDeleted(id);
    }

    @Operation(summary = "Получить записи по прогулке")
    @GetMapping(value = "/{id}/bookings", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Page<WalkDto> getWalkOrderDtos(@PathVariable String id, PageRequest pageRequest) {
//        return bookingService.getAll(id, pageRequest);
        return walkService.getOrdersByWalk(id, pageRequest);
    }

}
