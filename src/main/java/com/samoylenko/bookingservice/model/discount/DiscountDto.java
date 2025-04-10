package com.samoylenko.bookingservice.model.discount;

import com.samoylenko.bookingservice.model.voucher.DiscountType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@ToString
@Generated
@NoArgsConstructor
@AllArgsConstructor
public class DiscountDto {
    private String requiredRouteId;
    private DiscountType type;
    private DiscountStatus status;
    private String code;
    private BigDecimal priceForOne;
    private BigDecimal totalCost;
    private Integer quantity;
    private int discountPercent;
    private int discountAbsolute;
}

