package com.samoylenko.bookingservice.model.dto.booking;

import com.samoylenko.bookingservice.model.status.BookingStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto implements Serializable {
    private String id;
    private Instant createdDate;
    private Instant lastModifiedDate;
    private BookingStatus status;
    private Integer numberOfPeople;
    private Instant endTime;

    private String clientId;
    private String clientFirstName;
    private String clientLastName;

    private String walkId;
    private Instant walkStartTime;


    private String routeName;
    private Double totalCost;
}