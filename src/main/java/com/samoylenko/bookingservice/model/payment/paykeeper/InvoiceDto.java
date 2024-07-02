package com.samoylenko.bookingservice.model.payment.paykeeper;

import lombok.*;

import java.time.Instant;


@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDto {
    private String invoiceId;
    private String invoiceUrl;
    private Instant latestPaymentTime;
}
