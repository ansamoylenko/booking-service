package com.samoylenko.bookingservice.controller;

import com.samoylenko.bookingservice.model.dto.PaymentDto;
import com.samoylenko.bookingservice.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Платежи")
@AllArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @Operation(summary = "Получить платежи")
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Page<PaymentDto> getPayments(PageRequest pageRequest) {
        return paymentService.getPayments(pageRequest);
    }

    @Operation(summary = "Получить платеж по id")
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public PaymentDto getPaymentById(@PathVariable String id) {
        return paymentService.getPaymentById(id);
    }

}
