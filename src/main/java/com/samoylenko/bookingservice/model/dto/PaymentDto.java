package com.samoylenko.bookingservice.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.samoylenko.bookingservice.model.entity.PaymentEntity;
import lombok.Getter;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentDto extends BaseDto implements Serializable {
    private String id;
    private Instant createdDate;
    private Instant lastModifiedDate;
    private OrderDto orderEntity;
    private Integer amount;
}