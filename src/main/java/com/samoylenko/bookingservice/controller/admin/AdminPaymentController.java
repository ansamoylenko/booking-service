package com.samoylenko.bookingservice.controller.admin;

import com.samoylenko.bookingservice.model.payment.PaymentDto;
import com.samoylenko.bookingservice.model.payment.PaymentRequest;
import com.samoylenko.bookingservice.model.payment.PaymentStatus;
import com.samoylenko.bookingservice.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/v1/admin/payments")
@Tag(name = "Платежи (для администратора)")
@AllArgsConstructor
public class AdminPaymentController {
    private final PaymentService paymentService;

    @Operation(summary = "Получить платежи")
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Page<PaymentDto> getPayments(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "status", required = false) PaymentStatus status
    ) {
        var request = PaymentRequest.builder()
                .pageNumber(page)
                .pageSize(size)
                .status(status)
                .build();
        return paymentService.getPayments(request);
    }

    @Operation(summary = "Получить платеж по id")
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public PaymentDto getPaymentById(@PathVariable String id) {
        return paymentService.getPaymentById(id);
    }

}
