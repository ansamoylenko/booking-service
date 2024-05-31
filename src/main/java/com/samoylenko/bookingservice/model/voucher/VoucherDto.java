package com.samoylenko.bookingservice.model.voucher;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.samoylenko.bookingservice.model.status.VoucherStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Getter
@Setter
@SuperBuilder
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
