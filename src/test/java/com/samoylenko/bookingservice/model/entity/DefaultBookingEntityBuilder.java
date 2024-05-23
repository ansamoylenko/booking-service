package com.samoylenko.bookingservice.model.entity;

import com.samoylenko.bookingservice.model.status.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "of")
public class DefaultBookingEntityBuilder implements DefaultEntityBuilder<BookingEntity> {
    private BookingStatus status = BookingStatus.DRAFT;
    private WalkEntity walk;
    private Integer priceForOne = 3500;
    private Integer numberOfPeople = 1;
    private ContactEntity contact;
    private PaymentEntity payment;
    private String comment = "";
    private Boolean hasChildren = false;

    @Override
    public BookingEntity build() {
        return BookingEntity.builder()
                .status(status)
                .walk(walk)
                .priceForOne(priceForOne)
                .numberOfPeople(numberOfPeople)
                .contact(contact)
                .payment(payment)
                .comment(comment)
                .hasChildren(hasChildren)
                .build();
    }
}