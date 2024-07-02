package com.samoylenko.bookingservice.model.entity;

import com.samoylenko.bookingservice.model.route.RouteEntity;
import com.samoylenko.bookingservice.model.walk.WalkEntity;
import com.samoylenko.bookingservice.model.walk.WalkStatus;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "of")
public class DefaultWalkEntityBuilder implements DefaultEntityBuilder<WalkEntity> {
    private WalkStatus status = WalkStatus.DRAFT;
    private RouteEntity route;
    private Integer maxPlaces = 20;
    private Integer reservedPlaces = 0;
    private Integer availablePlaces = 20;
    private Integer priceForOne = 3500;
    private Instant startTime = Instant.parse("2024-05-01T00:00:00.00Z");
    private Instant endTime = startTime.plus(2, ChronoUnit.HOURS);
    private Integer duration = 120;

    @Override
    public WalkEntity build() {
        return WalkEntity.builder()
                .status(status)
                .route(route)
                .maxPlaces(maxPlaces)
                .reservedPlaces(reservedPlaces)
                .availablePlaces(availablePlaces)
                .priceForOne(priceForOne)
                .startTime(startTime)
                .endTime(endTime)
                .duration(duration)
                .build();
    }
}
