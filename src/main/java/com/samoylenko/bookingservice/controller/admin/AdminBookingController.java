package com.samoylenko.bookingservice.controller.admin;

import com.samoylenko.bookingservice.model.dto.booking.BookingCreateDto;
import com.samoylenko.bookingservice.model.dto.booking.BookingDto;
import com.samoylenko.bookingservice.model.dto.booking.CompositeBookingDto;
import com.samoylenko.bookingservice.model.dto.request.BookingRequest;
import com.samoylenko.bookingservice.model.status.BookingStatus;
import com.samoylenko.bookingservice.service.BookingService;
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
@RequestMapping("/api/v1/admin/bookings")
@Tag(name = "Бронирования (для администратора)")
@AllArgsConstructor
public class AdminBookingController {
    private final BookingService bookingService;

    @Operation(summary = "Добавить новую запись")
    @PostMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<CompositeBookingDto> createOrder(@RequestBody BookingCreateDto createDto, UriComponentsBuilder uriBuilder) {
        var crated = bookingService.create(createDto);
        var location = uriBuilder.path("/api/v1/admin/bookings/{id}")
                .buildAndExpand(crated.getId())
                .toUri();
        return ResponseEntity.created(location).body(crated);
    }

    @Operation(summary = "Получить страницу записей")
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Page<BookingDto> getOrders(
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "size", defaultValue = "10", required = false) int size,
            @RequestParam(value = "clientId", required = false) String clientId,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "status", required = false) List<BookingStatus> status,
            @RequestParam(value = "walkId", required = false) String walkId
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

    @Operation(summary = "Получить запись по id")
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public CompositeBookingDto getOrder(@PathVariable String id) {
        return bookingService.getBookingById(id);
    }

    @Operation(summary = "Обновить запись", hidden = true)
    @PatchMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public CompositeBookingDto updateOrder(@PathVariable String id, @RequestBody CompositeBookingDto dto) {
        return bookingService.updateOrder(id, dto);
    }

    @Operation(summary = "Отметить запись как удаленную")
    @DeleteMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable String id) {
        bookingService.deleteOrder(id);
    }
}
