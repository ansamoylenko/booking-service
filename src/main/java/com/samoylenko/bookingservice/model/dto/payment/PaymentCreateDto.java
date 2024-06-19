package com.samoylenko.bookingservice.model.dto.payment;

import com.samoylenko.bookingservice.model.dto.client.ClientDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreateDto implements Serializable {
    @NotBlank
    private String orderId;

    @NotBlank
    private String routeId;

    @NotBlank
    private String serviceName;

    @NotNull
    @Positive
    private Integer amount;

    @NotNull
    @PositiveOrZero
    private Integer priceForOne;

    private String voucher;

    @NotNull
    private ClientDto client;
    private Instant expiryTime;

}