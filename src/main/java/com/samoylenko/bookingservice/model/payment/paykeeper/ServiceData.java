package com.samoylenko.bookingservice.model.payment.paykeeper;

import java.util.List;

public record ServiceData(List<ServiceObject> cart, String lang) {
}
