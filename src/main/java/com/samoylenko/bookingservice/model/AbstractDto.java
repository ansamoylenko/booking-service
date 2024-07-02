package com.samoylenko.bookingservice.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Setter
@Getter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractDto {
    private String id;
    private Instant createdDate;
    private Instant lastModifiedDate;
}
