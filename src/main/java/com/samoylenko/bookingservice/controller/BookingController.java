package com.samoylenko.bookingservice.controller;

import com.samoylenko.bookingservice.model.dto.BookingDto;
import com.samoylenko.bookingservice.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/v1/bookings")
@Tag(name = "Бронирования")
@AllArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @Operation(summary = "Добавить новую запись")
    @PostMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createOrder(BookingDto BookingDto, UriComponentsBuilder uriBuilder) {
        var crated = bookingService.createOrder(BookingDto);
        var location = uriBuilder.path("/api/v1/bookings/{id}")
                .buildAndExpand(crated.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "Получить страницу записей")
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Page<BookingDto> getOrders(PageRequest pageRequest) {
        return bookingService.getOrders(pageRequest);
    }

    @Operation(summary = "Получить запись по id")
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public BookingDto getOrder(@PathVariable String id) {
        return bookingService.getOrderById(id);
    }

    @Operation(summary = "Обновить запись")
    @PatchMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public BookingDto updateOrder(@PathVariable String id, @RequestBody BookingDto BookingDto) {
        return bookingService.updateOrder(id, BookingDto);
    }

    @Operation(summary = "Отметить запись как удаленную")
    @DeleteMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable String id) {
        bookingService.deleteOrder(id);
    }
}
