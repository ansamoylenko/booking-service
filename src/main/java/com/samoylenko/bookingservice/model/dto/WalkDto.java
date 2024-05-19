package com.samoylenko.bookingservice.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.samoylenko.bookingservice.model.entity.WalkEntity;
import com.samoylenko.bookingservice.model.status.WalkStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link WalkEntity}
 */
@Getter
@Setter
@SuperBuilder
@JsonIgnoreProperties(ignoreUnknown = true)
public class WalkDto extends BaseDto implements Serializable {
    private WalkStatus status;
    @NotBlank(message = "Маршрут не может быть пустым")
    private String routeId;
    @Positive(message = "количество людей должно быть положительным")
    private Integer maxCount;
    @PositiveOrZero(message = "цена не может быть отрицательной")
    private Integer priceForOne;
    private Instant startTime;
    private Instant endTime;
}