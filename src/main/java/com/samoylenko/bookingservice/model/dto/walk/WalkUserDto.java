package com.samoylenko.bookingservice.model.dto.walk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.samoylenko.bookingservice.model.dto.route.RouteDto;
import com.samoylenko.bookingservice.model.entity.WalkEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link WalkEntity}
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WalkUserDto implements Serializable {
    private String id;
    private RouteDto route;
    private Integer availablePlaces;
    private Integer priceForOne;
    private Integer duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}