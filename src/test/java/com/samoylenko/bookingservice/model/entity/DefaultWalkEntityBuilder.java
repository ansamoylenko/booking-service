package com.samoylenko.bookingservice.model.entity;

import com.samoylenko.bookingservice.model.status.WalkStatus;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "of")
public class DefaultWalkEntityBuilder implements DefaultEntityBuilder<WalkEntity> {
    private WalkStatus status = WalkStatus.DRAFT;
    private RouteEntity route;
    private Integer maxPlaces = 20;
    private Integer reservedPlaces = 0;
    private Integer priceForOne = 3500;
    private LocalDateTime startTime = LocalDateTime.parse("2024-05-01T00:00:00");
    private LocalDateTime endTime = startTime.plusHours(2);
    private Set<EmployeeEntity> employees = new HashSet<>();

    @Override
    public WalkEntity build() {
        return WalkEntity.builder()
                .status(status)
                .route(route)
                .maxPlaces(maxPlaces)
                .reservedPlaces(reservedPlaces)
                .priceForOne(priceForOne)
                .startTime(startTime)
                .endTime(endTime)
                .employees(employees)
                .build();
    }
}
