package com.samoylenko.bookingservice.model.dto.request;

import com.samoylenko.bookingservice.model.status.WalkStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;

@With
@Getter
@Generated
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class WalkRequest extends BaseRequest {
    private String routeId;             // for user
    private LocalDateTime startAfter;   // for user
    private LocalDateTime startBefore;  // for user
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