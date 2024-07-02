package com.samoylenko.bookingservice.model.entity;

import com.samoylenko.bookingservice.model.promotion.DiscountStatus;
import com.samoylenko.bookingservice.model.status.PaymentStatus;
import com.samoylenko.bookingservice.model.voucher.DiscountType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.math.BigDecimal;
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
    private Instant latestPaymentTime = Instant.now();
    private String invoiceId = "test-invoice-id";
    private String invoiceUrl = "test-invoice-url";
    private Integer discountAbsolute = 0;
    private Integer discountPercent = 0;

    @Override
    public PaymentEntity build() {
        return PaymentEntity.builder()
                .status(status)
                .booking(booking)
                .quantity(amount)
                .priceForOne(BigDecimal.valueOf(priceForOne))
                .totalCost(BigDecimal.valueOf(totalCost))
                .latestPaymentTime(latestPaymentTime)
                .invoiceId(invoiceId)
                .invoiceUrl(invoiceUrl)
                .discountAbsolute(discountAbsolute)
                .discountPercent(discountPercent)
                .discountType(DiscountType.NONE)
                .discountStatus(DiscountStatus.NONE)
                .build();
    }
}
