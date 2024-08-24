package com.samoylenko.bookingservice.model.walk;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@With
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class WalkDto implements Serializable {
    private String id;
    private String routeId;
    private WalkStatus status;
    private Integer maxPlaces;
    private Integer availablePlaces;
    private Integer reservedPlaces;
    private Integer priceForOne;
    private Integer duration;
    private Instant startTime;
    private Instant endTime;
}
