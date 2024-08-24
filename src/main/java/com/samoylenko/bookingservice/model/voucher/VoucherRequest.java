package com.samoylenko.bookingservice.model.voucher;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class VoucherRequest {
    String route;
    VoucherStatus status;
    DiscountType type;
    Instant expiredAfter;
    Instant expiredBefore;
}
