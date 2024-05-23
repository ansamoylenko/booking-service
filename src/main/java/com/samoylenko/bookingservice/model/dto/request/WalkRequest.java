package com.samoylenko.bookingservice.model.dto.request;

import com.samoylenko.bookingservice.model.status.WalkStatus;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class WalkRequest {
    String routeId;             // for user
    LocalDateTime startAfter;   // for user
    LocalDateTime startBefore;  // for user
    Integer placeCount;         // for user

    Integer pageNumber;         // for user
    Integer pageSize;           // for user

    String employeeId;          // for admin
    WalkStatus status;          // for admin
}