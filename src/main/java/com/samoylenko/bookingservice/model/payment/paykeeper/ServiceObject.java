package com.samoylenko.bookingservice.model.payment.paykeeper;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ServiceObject {
    private String name;
    private String price;
    private int quantity;
    private String sum;
    private String tax;
    private String itemType;
    private String paymentType;
}
