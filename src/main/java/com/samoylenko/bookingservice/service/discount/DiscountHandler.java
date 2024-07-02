package com.samoylenko.bookingservice.service.discount;

import com.samoylenko.bookingservice.model.discount.DiscountDto;
import com.samoylenko.bookingservice.model.discount.DiscountRequest;

public interface DiscountHandler {
    DiscountHandler next();

    DiscountDto applyDiscount(DiscountRequest request);

    DiscountDto calculateDiscount(DiscountRequest discountRequest);
}
