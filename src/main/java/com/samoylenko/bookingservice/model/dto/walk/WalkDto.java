package com.samoylenko.bookingservice.model.dto.walk;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@With
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class WalkDto implements Serializable {
    private String id;
    private Integer availablePlaces;
    private Integer priceForOne;
    private Integer duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
