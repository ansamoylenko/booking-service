package com.samoylenko.bookingservice.model.dto.request;

import com.samoylenko.bookingservice.model.status.PaymentStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;

@With
@Getter
@Generated
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest extends BaseRequest {
    private PaymentStatus status;
}
