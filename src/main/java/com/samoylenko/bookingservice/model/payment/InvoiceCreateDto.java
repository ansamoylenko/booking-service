package com.samoylenko.bookingservice.model.payment;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceCreateDto {
    private BigDecimal cost;
    private BigDecimal price;
    private int quantity;
    private String clientId;
    private String orderId;
    private String clientEmail;
    private String clientPhone;
    private Instant expiry;
}
