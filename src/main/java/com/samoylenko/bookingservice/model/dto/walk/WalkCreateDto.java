package com.samoylenko.bookingservice.model.dto.walk;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;

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
    @NotBlank(message = "{invalid.notBlank}")
    private String routeId;

    @Schema(description = "Максимальное количество людей", example = "10", minimum = "1")
    @NotNull(message = "{invalid.notNull}")
    @Positive(message = "{invalid.positive}")
    private Integer maxPlaces;

    @Schema(description = "Цена за одно место", example = "1000", minimum = "0")
    @NotNull(message = "{invalid.notNull}")
    @Positive(message = "{invalid.positiveOrZero}")
    private Integer priceForOne;

    @Schema(description = "Время начала прогулки", example = "2024-06-01T06:00:00")
    @NotNull(message = "{invalid.notNull}")
    @FutureOrPresent(message = "{invalid.future}")
    private Instant startTime;

    @Schema(description = "Длительность прогулки в минутах", example = "150", minimum = "1")
    @NotNull(message = "{invalid.notNull}")
    @Positive(message = "{invalid.positive}")
    private Integer durationInMinutes;
}
