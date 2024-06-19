package com.samoylenko.bookingservice.model.dto.payment.paykeeper;

import java.util.List;

public record ServiceData(List<ServiceObject> cart, String lang) {
}
