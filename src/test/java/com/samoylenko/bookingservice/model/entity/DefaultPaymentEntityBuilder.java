package com.samoylenko.bookingservice.model.entity;

import com.samoylenko.bookingservice.model.status.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.time.Instant;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "of")
public class DefaultPaymentEntityBuilder implements DefaultEntityBuilder<PaymentEntity> {
    private PaymentStatus status = PaymentStatus.PENDING;
    private String orderId = "test-order-id";
    private BookingEntity booking;
    private Integer amount = 1;
    private String serviceName = "test-service-name";
    private Integer priceForOne = 3500;
    private Integer totalCost = 3500;
    private String link = "test-link";
    private Instant latestPaymentTime = Instant.now();
    private String invoiceId = "test-invoice-id";
    private String invoiceUrl = "test-invoice-url";

    @Override
    public PaymentEntity build() {
        return PaymentEntity.builder()
                .status(status)
                .orderId(orderId)
                .serviceName(serviceName)
                .booking(booking)
                .amount(amount)
                .priceForOne(priceForOne)
                .totalCost(totalCost)
                .invoiceUrl(link)
                .latestPaymentTime(latestPaymentTime)
                .invoiceId(invoiceId)
                .invoiceUrl(invoiceUrl)
                .build();
    }
}
