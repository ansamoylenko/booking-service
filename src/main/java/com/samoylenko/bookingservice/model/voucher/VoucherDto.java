package com.samoylenko.bookingservice.model.voucher;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    private DiscountType type;
    private VoucherStatus status;
    private String code;
    private String distributor;
    private String routeId;
    private String expectedRouteId;
    private Instant expiredAt;
    private int discountPercent;
    private int discountAbsolute;
}
