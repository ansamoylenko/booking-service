package com.samoylenko.bookingservice.model.payment;

import com.samoylenko.bookingservice.model.AbstractDto;
import com.samoylenko.bookingservice.model.discount.DiscountDto;
import com.samoylenko.bookingservice.model.payment.paykeeper.InvoiceDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@With
@Setter
@Getter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto extends AbstractDto implements Serializable {
    private PaymentStatus status;
    private String bookingId;
    private int quantity;
    private BigDecimal priceForOne;
    private BigDecimal totalCost;
    private InvoiceDto invoice;
    private DiscountDto discount;
    private Instant latestPaymentTime;
}