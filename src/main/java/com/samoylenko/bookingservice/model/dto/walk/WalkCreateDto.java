package com.samoylenko.bookingservice.model.dto.walk;

import com.samoylenko.bookingservice.model.entity.WalkEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for creating {@link WalkEntity}
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalkCreateDto implements Serializable {
    @Schema(description = "Идентификатор маршрута", example = "69472cd9-f395-4064-ba87-c7d5192dfe7f")
    @NotBlank(message = "не указан маршрут")
    private String routeId;

    @Schema(description = "Максимальное количество людей", example = "10", minimum = "1")
    @NotNull(message = "не указано максимальное количество людей")
    @Positive(message = "количество людей должно быть положительным")
    private Integer maxPlaces;

    @Schema(description = "Цена за одно место", example = "1000", minimum = "0")
    @NotNull(message = "не указана цена")
    @PositiveOrZero(message = "цена не может быть отрицательной")
    private Integer priceForOne;

    @Schema(description = "Время начала прогулки", example = "2024-06-01T06:00:00")
    @NotNull(message = "не указана дата начала прогулки")
    @FutureOrPresent(message = "время начала прогулки должно быть в будущем")
    private LocalDateTime startTime;

    @Schema(description = "Длительность прогулки в минутах", example = "150", minimum = "1")
    @NotNull(message = "не указана продолжительность прогулки")
    @Positive(message = "продолжительность маршрута должна быть положительной")
    private Integer durationInMinutes;
}
