package com.samoylenko.bookingservice.model.voucher;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.samoylenko.bookingservice.model.status.VoucherStatus;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@Generated
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class VoucherDto {
    private String id;
    private VoucherType type;
    private VoucherStatus status;
    private String code;
    private String distributor;
    private String routeId;
    private Instant expiredAt;
    private int discountPercent;
    private int discountAbsolute;
}
