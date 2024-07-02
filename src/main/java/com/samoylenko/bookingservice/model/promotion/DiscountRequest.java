package com.samoylenko.bookingservice.model.promotion;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DiscountRequest {
    private String code;
    @NotNull
    private Integer quantity;
    @NotNull
    private BigDecimal price;
    private String routeId;
    private String phone;
}
