package com.samoylenko.bookingservice.model.dto.booking;

import com.samoylenko.bookingservice.model.dto.client.ClientDto;
import com.samoylenko.bookingservice.model.dto.payment.PaymentDto;
import com.samoylenko.bookingservice.model.status.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CompositeBookingDto implements Serializable {
    private String id;
    private Instant createdDate;
    private Instant lastModifiedDate;
    private BookingStatus status;
    private String walkId;
    private Integer numberOfPeople;
    private BookingInfo info;
    private ClientDto client;
    private PaymentDto payment;
    private Duration timeLeft;
}