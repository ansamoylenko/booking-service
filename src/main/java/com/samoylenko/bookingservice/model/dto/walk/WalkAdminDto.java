package com.samoylenko.bookingservice.model.dto.walk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.samoylenko.bookingservice.model.dto.booking.BookingAdminDto;
import com.samoylenko.bookingservice.model.dto.route.RouteDto;
import com.samoylenko.bookingservice.model.entity.WalkEntity;
import com.samoylenko.bookingservice.model.status.WalkStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class WalkAdminDto implements Serializable {
    private String id;
    private Instant createdDate;
    private Instant lastModifiedDate;

    private WalkStatus status;
    private RouteDto route;
    private Integer maxPlaces;
    private Integer availablePlaces;
    private Integer reservedPlaces;
    private Integer priceForOne;
    private Integer duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<BookingAdminDto> bookings;
}