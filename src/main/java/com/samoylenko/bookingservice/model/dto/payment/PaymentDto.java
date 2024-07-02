package com.samoylenko.bookingservice.model.dto.payment;

import com.samoylenko.bookingservice.model.dto.AbstractDto;
import com.samoylenko.bookingservice.model.dto.payment.paykeeper.InvoiceDto;
import com.samoylenko.bookingservice.model.promotion.DiscountDto;
import com.samoylenko.bookingservice.model.status.PaymentStatus;
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