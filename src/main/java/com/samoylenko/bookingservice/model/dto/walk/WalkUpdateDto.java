package com.samoylenko.bookingservice.model.dto.walk;

import com.samoylenko.bookingservice.model.entity.WalkEntity;
import com.samoylenko.bookingservice.model.status.WalkStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO for updating {@link WalkEntity}
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalkUpdateDto {
    @Schema(description = "Статус прогулки", example = "CANCELED")
    private WalkStatus status;

    @Schema(description = "Максимальное количество людей", example = "10")
    @Positive(message = "количество людей должно быть положительным")
    private Integer maxPlaces;

    @Schema(description = "Цена за одно место", example = "1000")
    @PositiveOrZero(message = "цена не может быть отрицательной")
    private Integer priceForOne;

    @Schema(description = "Время начала прогулки", example = "2024-06-01T06:00:00")
    @FutureOrPresent(message = "время начала прогулки должно быть в будущем")
    private LocalDateTime startTime;

    @Schema(description = "Длительность прогулки в минутах", example = "150")
    @Positive(message = "продолжительность маршрута должна быть положительной")
    private Integer durationInMinutes;
}
