package com.samoylenko.bookingservice.model.route;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.samoylenko.bookingservice.model.BaseDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link RouteEntity}
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RouteDto extends BaseDto implements Serializable {
    @NotBlank(message = "{invalid.notBlank}")
    private String name;
    @NotBlank(message = "{invalid.notBlank}")
    private String description;
    @PositiveOrZero(message = "цена не может быть отрицательной")
    private Integer priceForOne;
    private Instant createdDate;
    private Instant lastModifiedDate;
}