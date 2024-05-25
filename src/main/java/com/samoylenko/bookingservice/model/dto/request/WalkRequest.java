package com.samoylenko.bookingservice.model.dto.request;

import com.samoylenko.bookingservice.model.status.WalkStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;

@Getter
@Builder
@Generated
@AllArgsConstructor
@NoArgsConstructor
public class WalkRequest {
    @Schema(description = "Идентификатор маршрута", example = "3f5d6702-8554-4137-85e0-4ada615e7253")
    private String routeId;             // for user

    @Schema(description = "Минимальное время начала прогулки", example = "2024-01-01T00:00:00")
    private LocalDateTime startAfter;   // for user

    @Schema(description = "Максимальное время начала прогулки", example = "2024-12-01T00:00:00")
    private LocalDateTime startBefore;  // for user

    @Schema(description = "Количество доступных мест", example = "2")
    private Integer availablePlaces;         // for user

    @Schema(description = "Номер страницы", example = "0")
    private Integer pageNumber;         // for user
    @Schema(description = "Размер страницы", example = "10")
    private Integer pageSize;         // for user

    @Schema(description = "Идентификатор сотрудника", example = "3f5d6702-8554-4137-85e0-4ada615e7253")
    private String employeeId;          // for admin

    @Schema(description = "Статус прогулки", example = "Запись активна")
    private WalkStatus status;          // for admin

    public PageRequest getPageRequest() {
        return PageRequest.of(
                pageNumber == null ? 0 : pageNumber,
                pageSize == null ? 10 : pageSize,
                Sort.by(Sort.Direction.ASC, "startTime"));
    }
}