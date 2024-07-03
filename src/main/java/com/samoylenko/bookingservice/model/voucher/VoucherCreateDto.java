package com.samoylenko.bookingservice.model.voucher;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Generated
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class VoucherCreateDto {
    @NotNull
    @Schema(description = "Тип ваучера", requiredMode = Schema.RequiredMode.REQUIRED)
    private DiscountType type;

    @Schema(description = "Код", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @Schema(description = "Тот кто будет распространять ваучер", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String distributor;

    @Schema(description = "На какой маршрут распространять ваучер", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String routeId;

    @Schema(description = "Срок действия ваучера", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Instant expiredAt;

    @Min(0)
    @Max(100)
    @Schema(description = "Процент скидки (от 0 до 100)", requiredMode = Schema.RequiredMode.REQUIRED)
    private int discountPercent;

    @Min(0)
    @Schema(description = "Сумма скидки", requiredMode = Schema.RequiredMode.REQUIRED)
    private int discountAbsolute;
}
