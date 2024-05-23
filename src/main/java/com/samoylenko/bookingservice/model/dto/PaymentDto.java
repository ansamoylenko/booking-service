package com.samoylenko.bookingservice.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.samoylenko.bookingservice.model.dto.booking.BookingDto;
import com.samoylenko.bookingservice.model.entity.PaymentEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link PaymentEntity}
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentDto extends BaseDto implements Serializable {
    private String id;
    private Instant createdDate;
    private Instant lastModifiedDate;
    private BookingDto orderEntity;
    private Integer amount;
}