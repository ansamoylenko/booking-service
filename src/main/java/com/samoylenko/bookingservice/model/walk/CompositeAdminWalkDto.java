package com.samoylenko.bookingservice.model.walk;

import com.samoylenko.bookingservice.model.booking.CompositeBookingDto;
import com.samoylenko.bookingservice.model.route.RouteDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

/**
 * DTO for {@link WalkEntity} for admin
 */
@Getter
@Setter
@With
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CompositeAdminWalkDto implements Serializable {
    private String id;
    private Integer availablePlaces;
    private Integer priceForOne;
    private Integer duration;
    private Instant startTime;
    private Instant endTime;

    private RouteDto route;

    private WalkStatus status;
    private Instant createdDate;
    private Instant lastModifiedDate;
    private Integer maxPlaces;
    private Integer reservedPlaces;
    private List<CompositeBookingDto> bookings;
}