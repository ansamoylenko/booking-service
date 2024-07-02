package com.samoylenko.bookingservice.model.route;

import com.samoylenko.bookingservice.annotations.Title;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;

/**
 * DTO for creating {@link RouteEntity}
 */
@Getter
@Setter
@Builder
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class RouteUpdateDto implements Serializable {
    @Title(message = "название маршрута не может быть пустым", nullable = true)
    @Schema(description = "Название маршрута", example = "Вуокса")
    private String name;

    @Title(message = "описание маршрута не может быть пустым", nullable = true)
    @Schema(description = "Описание маршрута", example = "Сплав по реке Вуокса")
    private String description;

    @Schema(description = "цена за одного человека", example = "3500")
    @PositiveOrZero(message = "цена не может быть отрицательной")
    private Integer priceForOne;
}