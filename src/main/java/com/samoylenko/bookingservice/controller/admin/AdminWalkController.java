package com.samoylenko.bookingservice.controller.admin;

import com.samoylenko.bookingservice.model.dto.booking.BookingDto;
import com.samoylenko.bookingservice.model.dto.request.BookingRequest;
import com.samoylenko.bookingservice.model.dto.request.WalkRequest;
import com.samoylenko.bookingservice.model.dto.walk.CompositeAdminWalkDto;
import com.samoylenko.bookingservice.model.dto.walk.WalkCreateDto;
import com.samoylenko.bookingservice.model.dto.walk.WalkDto;
import com.samoylenko.bookingservice.model.dto.walk.WalkUpdateDto;
import com.samoylenko.bookingservice.model.status.BookingStatus;
import com.samoylenko.bookingservice.model.status.WalkStatus;
import com.samoylenko.bookingservice.service.BookingService;
import com.samoylenko.bookingservice.service.WalkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/v1/admin/walks")
@Tag(name = "Прогулки (для администратора)")
@AllArgsConstructor
public class AdminWalkController {
    private final WalkService walkService;
    private final BookingService bookingService;

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
            @RequestParam(value = "status", required = false) WalkStatus status
    ) {
        var request = WalkRequest.builder()
                .routeId(routeId)
                .startAfter(startAfter)
                .startBefore(startBefore)
                .availablePlaces(availablePlaces)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .employeeId(employeeId)
                .status(status)
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
    @GetMapping(value = "/{walkId}/bookings", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Page<BookingDto> getWalkBookings(
            @PathVariable(value = "walkId") String walkId,
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "size", defaultValue = "10", required = false) int size,
            @RequestParam(value = "clientId", required = false) String clientId,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "status", required = false) BookingStatus status
    ) {
        var request = BookingRequest.builder()
                .pageNumber(page)
                .pageSize(size)
                .clientId(clientId)
                .clientPhone(phone)
                .clientEmail(email)
                .status(status)
                .walkId(walkId)
                .build();
        return bookingService.getBookings(request);
    }

}