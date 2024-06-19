package com.samoylenko.bookingservice.model.entity;

import com.samoylenko.bookingservice.model.status.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.time.Instant;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "of")
public class DefaultBookingEntityBuilder implements DefaultEntityBuilder<BookingEntity> {
    private BookingStatus status = BookingStatus.ACTIVE;
    private WalkEntity walk;
    private Integer priceForOne = 3500;
    private Integer numberOfPeople = 1;
    private ClientEntity client;
    private PaymentEntity payment;
    private String comment = "";
    private Boolean hasChildren = false;
    private Instant endTime = Instant.now().plusSeconds(60);

    @Override
    public BookingEntity build() {
        return BookingEntity.builder()
                .status(status)
                .walk(walk)
                .numberOfPeople(numberOfPeople)
                .client(client)
                .payment(payment)
                .comment(comment)
                .hasChildren(hasChildren)
                .endTime(endTime)
                .build();
    }
}
