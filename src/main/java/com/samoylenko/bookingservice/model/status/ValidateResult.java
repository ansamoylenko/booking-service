package com.samoylenko.bookingservice.model.status;

import com.samoylenko.bookingservice.model.voucher.VoucherType;
import lombok.*;

@Generated
@With
@Value
@Builder
@ToString
public class ValidateResult {
    String id;
    @ToString.Include
    Status status;
    VoucherType type;
    int discountPercent;
    int discountAbsolute;

    public enum Status {
        VALID,
        EXPIRED,
        NOT_VALID
    }
}

