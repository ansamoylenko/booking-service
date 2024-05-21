package com.samoylenko.bookingservice.model.entity;

import com.samoylenko.bookingservice.model.status.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "of")
public class DefaultPaymentEntityBuilder implements DefaultEntityBuilder<PaymentEntity> {
    private PaymentStatus status = PaymentStatus.PENDING;
    private BookingEntity bookingEntity;
    private Integer amount = 3500;

    @Override
    public PaymentEntity build() {
        return PaymentEntity.builder()
                .status(status)
                .booking(bookingEntity)
                .amount(amount)
                .build();
    }
}
