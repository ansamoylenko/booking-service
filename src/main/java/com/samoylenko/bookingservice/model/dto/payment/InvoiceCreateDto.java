package com.samoylenko.bookingservice.model.dto.payment;

import com.samoylenko.bookingservice.model.dto.payment.paykeeper.ServiceData;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Builder
public class InvoiceCreateDto {

    @NotNull
    private BigDecimal payAmount;

    @NotBlank
    private String clientId;

    private String orderId;

    @Email
    private String clientEmail;

    private ServiceData serviceData;

    private String clientPhone;

    private Instant expiry;
}
