package com.samoylenko.bookingservice.model.dto.walk;

import com.samoylenko.bookingservice.model.dto.route.RouteDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@With
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CompositeUserWalkDto {
    private String id;
    private RouteDto route;
    private Integer availablePlaces;
    private Integer priceForOne;
    private Integer duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
