package com.samoylenko.bookingservice.model.walk;

import com.samoylenko.bookingservice.model.BaseRequest;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.Instant;

@With
@Getter
@Generated
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class WalkRequest extends BaseRequest {
    private String routeId;             // for user
    private Instant startAfter;   // for user
    private Instant startBefore;  // for user
    private Instant endAfter;    // for user
    private Instant endBefore;   // for user
    private Integer availablePlaces;    // for user
    private String employeeId;          // for admin
    private WalkStatus status;          // for admin

    public PageRequest getPageRequest() {
        return PageRequest.of(
                pageNumber == null ? 0 : pageNumber,
                pageSize == null ? 10 : pageSize,
                Sort.by(Sort.Direction.ASC, "startTime"));
    }
}