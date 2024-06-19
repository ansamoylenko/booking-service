package com.samoylenko.bookingservice.model.dto.payment.paykeeper;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ServiceObject {
    private String name;
    private float price;
    private int quantity;
    private float sum;
    private String tax;
    private String itemType;
    private String paymentType;
}
