package com.samoylenko.bookingservice.service.discount;

import com.samoylenko.bookingservice.config.ServiceProperties;
import com.samoylenko.bookingservice.model.promotion.DiscountDto;
import com.samoylenko.bookingservice.model.promotion.DiscountRequest;
import com.samoylenko.bookingservice.model.promotion.DiscountStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.RoundingMode;

import static com.samoylenko.bookingservice.model.voucher.DiscountType.GROUP_BOOKING;
import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;


/**
 * Обработчик для акции "Скидка от N людей"
 */
@Getter
@AllArgsConstructor
public class GroupDiscountHandler implements DiscountHandler {
    private final DiscountHandler next;
    private final ServiceProperties serviceProperties;


    @Override
    public DiscountHandler next() {
        return next;
    }

    @Override
    public DiscountDto applyDiscount(DiscountRequest request) {
        return calculateDiscount(request);
    }

    @Override
    public DiscountDto calculateDiscount(DiscountRequest discountRequest) {
        if (!serviceProperties.isGroupDiscountEnabled()) return null;
        var minQuantity = serviceProperties.getGroupDiscountMinPlaces();
        if (discountRequest.getQuantity() < minQuantity) return null;
        var price = discountRequest.getPrice();
        var quantity = valueOf(discountRequest.getQuantity());
        var discountPercent = serviceProperties.getGroupDiscountValuePercent();
        var discountAbsolute = valueOf(serviceProperties.getGroupDiscountValueAbsolute());
        var resultCost = price
                .subtract(discountAbsolute)
                .multiply(quantity)
                .multiply(valueOf(100 - discountPercent))
                .divide(valueOf(100), 2, RoundingMode.UP);
        resultCost = resultCost.compareTo(ZERO) > 0 ? resultCost : ZERO;
        var resultPrice = resultCost.divide(quantity, 2, RoundingMode.UP);
        return DiscountDto.builder()
                .type(GROUP_BOOKING)
                .code(discountRequest.getCode())
                .status(DiscountStatus.ACTIVE)
                .discountPercent(discountPercent)
                .discountAbsolute(discountAbsolute.intValue())
                .quantity(discountRequest.getQuantity())
                .priceForOne(resultPrice)
                .totalCost(resultCost)
                .build();
    }
}
