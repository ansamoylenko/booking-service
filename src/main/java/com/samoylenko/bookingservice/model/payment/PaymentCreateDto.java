package com.samoylenko.bookingservice.model.payment;

import com.samoylenko.bookingservice.model.client.ClientDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreateDto implements Serializable {
    @NotBlank
    private String bookingId;

    @NotBlank
    private String routeId;

    @NotBlank
    private String serviceName;

    @NotNull
    @Positive
    private Integer quantity;

    @NotNull
    @PositiveOrZero
    private BigDecimal priceForOne;

    private String voucher;

    @NotNull
    private ClientDto client;

    @NotNull
    private Instant expiryTime;
}