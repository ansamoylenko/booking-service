package com.samoylenko.bookingservice.model.payment;

import com.samoylenko.bookingservice.model.BaseRequest;
import lombok.*;
import lombok.experimental.SuperBuilder;

@With
@Getter
@Generated
@SuperBuilder
@NoArgsConstructor(staticName = "of")
@AllArgsConstructor
public class PaymentRequest extends BaseRequest {
    private PaymentStatus status;
}
