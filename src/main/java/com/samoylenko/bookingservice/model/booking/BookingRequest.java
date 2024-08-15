package com.samoylenko.bookingservice.model.booking;

import com.samoylenko.bookingservice.model.BaseRequest;
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
    private String routeId;
}
