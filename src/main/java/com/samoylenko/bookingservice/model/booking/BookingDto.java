package com.samoylenko.bookingservice.model.booking;

import com.samoylenko.bookingservice.model.client.ClientDto;
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
    private ClientDto client;
    private String walkId;
    private Instant walkStartTime;
    private String routeName;
    private Double totalCost;
}