package com.samoylenko.bookingservice.model.dto.payment;

import com.samoylenko.bookingservice.model.status.PaymentStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;

@With
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto implements Serializable {
    private String id;
    private Instant createdDate;
    private Instant lastModifiedDate;
    private String orderId;
    private PaymentStatus status;
    private String serviceName;
    private int amount;
    private int priceForOne;
    private int totalCost;
    private Duration timeToPay;
    private String link;
}