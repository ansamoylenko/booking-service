package com.samoylenko.bookingservice.model.dto.booking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.samoylenko.bookingservice.model.dto.BaseDto;
import com.samoylenko.bookingservice.model.dto.ContactDto;
import com.samoylenko.bookingservice.model.entity.BookingEntity;
import com.samoylenko.bookingservice.model.status.BookingStatus;
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
 * DTO for {@link BookingEntity}
 *
 * @author Alexander Samoylenko
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingAdminDto extends BaseDto implements Serializable {
    private BookingStatus status;
    @NotNull(message = "{invalid.notNull}")
    private String walkId;
    @PositiveOrZero(message = "цена не может быть отрицательной")
    private Integer priceForOne;
    @Positive(message = "количество людей должно быть положительным")
    private Integer numberOfPeople;
    private ContactDto contact;
    private String comment;
    private Boolean hasChildren;
}