package com.samoylenko.bookingservice.controller.admin;

import com.samoylenko.bookingservice.model.booking.*;
import com.samoylenko.bookingservice.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Operation(summary = "Добавить новую запись", description = "Доступен для роли MANAGER и выше")
    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<CompositeBookingDto> createBooking(@RequestBody BookingCreateDto createDto, UriComponentsBuilder uriBuilder) {
        var crated = bookingService.create(createDto);
        var location = uriBuilder.path("/api/v1/admin/bookings/{id}")
                .buildAndExpand(crated.getId())
                .toUri();
        return ResponseEntity.created(location).body(crated);
    }

    @Operation(summary = "Создать счет на оплату")
    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping(value = "/{id}/invoice", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public CompositeBookingDto createInvoice(
            @PathVariable String id,
            @RequestParam(value = "voucher", required = false) String voucher
    ) {
        return bookingService.createInvoice(id, voucher);
    }

    @Operation(summary = "Добавить сотрудника в бронирование", description = "Доступен для роли MANAGER и выше")
    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping(value = "/{id}/employee", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public AdminBookingDto addEmployee(@PathVariable String id, @RequestBody String employeeId) {
        return bookingService.addEmployee(id, employeeId);
    }

    @Operation(summary = "Удалить сотрудника из бронирования", description = "Доступен для роли MANAGER и выше")
    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping(value = "/{id}/employee", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public AdminBookingDto removeEmployee(@PathVariable String id, @RequestBody String employeeId) {
        return bookingService.removeEmployee(id, employeeId);
    }

    @Operation(summary = "Получить страницу записей", description = "Доступен для роли MANAGER и выше")
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Page<BookingDto> getOrders(
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "size", defaultValue = "10", required = false) int size,
            @RequestParam(value = "clientId", required = false) String clientId,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "status", required = false) List<BookingStatus> status,
            @RequestParam(value = "walkId", required = false) String walkId,
            @RequestParam(value = "routeId", required = false) String routeId
    ) {
        var request = BookingRequest.builder()
                .pageNumber(page)
                .pageSize(size)
                .clientId(clientId)
                .clientPhone(phone)
                .clientEmail(email)
                .status(status)
                .walkId(walkId)
                .routeId(routeId)
                .build();
        return bookingService.getBookings(request);
    }

    @Operation(summary = "Получить запись по id", description = "Доступен для роли MANAGER и выше")
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public AdminBookingDto getBookingById(@PathVariable String id) {
        return bookingService.getBookingForAdmin(id);
    }

    @Operation(summary = "Отметить запись как удаленную", description = "Доступен для роли ADMIN и выше")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable String id) {
        bookingService.deleteOrder(id);
    }
}
