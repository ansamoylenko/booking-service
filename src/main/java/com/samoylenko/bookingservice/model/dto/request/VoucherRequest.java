package com.samoylenko.bookingservice.model.dto.request;

import com.samoylenko.bookingservice.model.promotion.VoucherStatus;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class VoucherRequest {
    String route;
    VoucherStatus status;
}
