package com.samoylenko.bookingservice.model.dto.route;

import com.samoylenko.bookingservice.annotations.Title;
import com.samoylenko.bookingservice.model.entity.RouteEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.io.Serializable;

/**
 * DTO for creating {@link RouteEntity}
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteCreateDto implements Serializable {
    @Schema(description = "Название маршрута", example = "Вуокса", requiredMode = Schema.RequiredMode.REQUIRED)
    @Title(message = "название маршрута не может быть пустым")
    private String name;

    @Schema(description = "Описание маршрута", example = "Сплав по реке Вуокса", requiredMode = Schema.RequiredMode.REQUIRED)
    @Title(message = "описание маршрута не может быть пустым")
    private String description;

    @Schema(description = "Цена за одного человека", example = "3500", requiredMode = Schema.RequiredMode.REQUIRED)
    @PositiveOrZero(message = "цена не может быть отрицательной")
    private Integer priceForOne;

    @Schema(description = "Название услуги в чеке", example = "Прогулка", requiredMode = Schema.RequiredMode.REQUIRED)
    @Title(message = "название услуги не может быть пустым")
    private String serviceName;
}