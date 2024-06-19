package com.samoylenko.bookingservice.model.dto.request;

import com.samoylenko.bookingservice.model.status.BookingStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@With
@Getter
@Generated
@SuperBuilder
@NoArgsConstructor(staticName = "of")
@AllArgsConstructor
public class BookingRequest extends BaseRequest {
    private String walkId;
    private List<BookingStatus> status;
    private String clientPhone;
    private String clientEmail;
    private String clientId;
}
