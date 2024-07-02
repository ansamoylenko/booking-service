package com.samoylenko.bookingservice.model.voucher;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class VoucherRequest {
    String route;
    VoucherStatus status;
}
