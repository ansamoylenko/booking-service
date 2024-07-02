package com.samoylenko.bookingservice.model.booking;

import com.samoylenko.bookingservice.model.client.ClientCreateDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookingCreateDto implements Serializable {
    @Schema(description = "Идентификатор прогулки", example = "69472cd9-f395-4064-ba87-c7d5192dfe7f")
    @NotBlank(message = "{invalid.notBlank}")
    private String walkId;

    @Schema(description = "Количество людей", example = "1")
    @NotNull(message = "{invalid.notNull}")
    @Positive(message = "{invalid.positive}")
    private Integer numberOfPeople;

    @NotNull(message = "{invalid.notNull}")
    private ClientCreateDto client;

    @NotNull(message = "{invalid.notNull}")
    private BookingInfo bookingInfo;
}
