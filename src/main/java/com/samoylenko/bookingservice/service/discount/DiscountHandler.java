package com.samoylenko.bookingservice.service.discount;

import com.samoylenko.bookingservice.model.promotion.DiscountDto;
import com.samoylenko.bookingservice.model.promotion.DiscountRequest;

public interface DiscountHandler {
    DiscountHandler next();

    DiscountDto applyDiscount(DiscountRequest request);

    DiscountDto calculateDiscount(DiscountRequest discountRequest);
}
