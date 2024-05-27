package com.samoylenko.bookingservice.model.dto.walk;

import com.samoylenko.bookingservice.model.dto.booking.CompositeBookingDto;
import com.samoylenko.bookingservice.model.dto.route.RouteDto;
import com.samoylenko.bookingservice.model.entity.EmployeeEntity;
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
public class CompositeAdminWalkDto implements Serializable {
    private String id;
    private Integer availablePlaces;
    private Integer priceForOne;
    private Integer duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private RouteDto route;

    private WalkStatus status;
    private Instant createdDate;
    private Instant lastModifiedDate;
    private Integer maxPlaces;
    private Integer reservedPlaces;
    private List<CompositeBookingDto> bookings;
    private List<EmployeeEntity> employees;
}