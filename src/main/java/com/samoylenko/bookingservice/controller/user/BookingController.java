package com.samoylenko.bookingservice.controller.user;


import com.samoylenko.bookingservice.model.dto.booking.BookingCreateDto;
import com.samoylenko.bookingservice.model.dto.booking.CompositeBookingDto;
import com.samoylenko.bookingservice.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/v1/bookings")
@Tag(name = "Бронирования (для пользователей)")
@AllArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @Operation(summary = "Создать бронирование")
    @PostMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<CompositeBookingDto> createOrder(@RequestBody BookingCreateDto createDto, UriComponentsBuilder uriBuilder) {
        var created = bookingService.create(createDto);
        var location = uriBuilder.path("/api/v1/bookings/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Создать счет на оплату")
    @PostMapping(value = "/{id}/invoice", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public CompositeBookingDto createInvoice(
            @PathVariable String id,
            @RequestParam(value = "voucher", required = false) String voucher
    ) {
        return bookingService.createInvoice(id, voucher);
    }


    @Operation(summary = "Получить бронирование")
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public CompositeBookingDto getOrder(@PathVariable String id) {
        return bookingService.getBookingById(id);
    }
}
