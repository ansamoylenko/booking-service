package com.samoylenko.bookingservice.service.discount;

import com.samoylenko.bookingservice.config.ServiceProperties;
import com.samoylenko.bookingservice.model.dto.request.BookingRequest;
import com.samoylenko.bookingservice.model.promotion.DiscountDto;
import com.samoylenko.bookingservice.model.promotion.DiscountRequest;
import com.samoylenko.bookingservice.model.promotion.DiscountStatus;
import com.samoylenko.bookingservice.model.status.BookingStatus;
import com.samoylenko.bookingservice.service.BookingService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.math.RoundingMode;
import java.util.List;

import static com.samoylenko.bookingservice.model.voucher.DiscountType.REPEATED_BOOKING;
import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;


/**
 * Обработчик для акции "Скидка старым клиентам"
 */
@Slf4j
@Getter
@AllArgsConstructor
public class RepeatedBookingDiscountHandler implements DiscountHandler {
    private final DiscountHandler next;
    private final ServiceProperties serviceProperties;
    private final BookingService bookingService;

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
        if (!serviceProperties.isRepeatedBookingDiscountEnabled()) return null;
        var bookingRequest = BookingRequest.builder()
                .clientPhone(discountRequest.getPhone())
                .status(List.of(BookingStatus.COMPLETED))
                .build();
        var bookingPage = bookingService.getBookings(bookingRequest);
        if (bookingPage.getTotalElements() == 0) {
            return null;
        }

        var price = discountRequest.getPrice();
        var quantity = valueOf(discountRequest.getQuantity());
        var discountPercent = serviceProperties.getRepeatedBookingDiscountPercent();
        var discountAbsolute = valueOf(serviceProperties.getRepeatedBookingDiscountAbsolute());
        var resultCost = price
                .subtract(discountAbsolute)
                .multiply(quantity)
                .multiply(valueOf(100 - discountPercent))
                .divide(valueOf(100), 2, RoundingMode.UP);
        resultCost = resultCost.compareTo(ZERO) > 0 ? resultCost : ZERO;
        var resultPrice = resultCost.divide(quantity, 2, RoundingMode.UP);
        return DiscountDto.builder()
                .type(REPEATED_BOOKING)
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
