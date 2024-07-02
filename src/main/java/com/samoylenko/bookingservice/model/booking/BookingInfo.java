package com.samoylenko.bookingservice.model.booking;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookingInfo {
    @Schema(description = "Комментарий к бронированию", example = " какой-то комментарий", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String comment;

    @Schema(description = "Галка о том что будут присутствовать дети", example = "false", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean hasChildren;

    @Schema(description = "Подтверждение согласия на бронирование и обработку персональных данных", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean agreementConfirmed;
}
