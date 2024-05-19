package com.samoylenko.bookingservice.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.samoylenko.bookingservice.model.entity.OrderEntity;
import com.samoylenko.bookingservice.model.status.OrderStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * DTO for {@link OrderEntity}
 *
 * @author Alexander Samoylenko
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderDto extends BaseDto implements Serializable {
    private OrderStatus status;
    @NotNull(message = "walkId не может быть пустым")
    private String walkId;
    @PositiveOrZero(message = "цена не может быть отрицательной")
    private Integer priceForOne;
    @Positive(message = "количество людей должно быть положительным")
    private Integer numberOfPeople;
    private ContactDto contact;
    private String comment;
    private Boolean hasChildren;
}