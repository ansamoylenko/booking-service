package com.samoylenko.bookingservice.service.discount;

import com.samoylenko.bookingservice.config.ServiceProperties;
import com.samoylenko.bookingservice.model.promotion.DiscountDto;
import com.samoylenko.bookingservice.model.promotion.DiscountRequest;
import com.samoylenko.bookingservice.model.promotion.DiscountStatus;
import com.samoylenko.bookingservice.model.voucher.DiscountType;
import com.samoylenko.bookingservice.service.BookingService;
import com.samoylenko.bookingservice.service.PromotionService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Getter
@Component
public class DiscountManager implements DiscountHandler {
    private DiscountHandler firstHandler;
    private final ServiceProperties serviceProperties;
    private final PromotionService promotionService;
    private final BookingService bookingService;

    public DiscountManager(ServiceProperties serviceProperties, PromotionService promotionService, @Lazy BookingService bookingService) {
        this.serviceProperties = serviceProperties;
        this.promotionService = promotionService;
        this.bookingService = bookingService;
    }

    @PostConstruct
    public void init() {
        var repeatedBookingDiscountHandler = new RepeatedBookingDiscountHandler(null, serviceProperties, bookingService);
        var groupDiscountHandler = new GroupDiscountHandler(repeatedBookingDiscountHandler, serviceProperties);
        var promocodHandler = new PromocodHandler(groupDiscountHandler, promotionService);
        var certificateHandler = new CertificateHandler(promocodHandler, promotionService);
        this.firstHandler = certificateHandler;
    }

    @Override
    public DiscountHandler next() {
        return firstHandler;
    }

    @Override
    public DiscountDto applyDiscount(DiscountRequest discountRequest) {
        var handler = firstHandler;
        while (handler != null) {
            var discount = handler.applyDiscount(discountRequest);
            if (discount != null) {
                return discount;
            }
            handler = handler.next();
        }
        var cost = discountRequest.getPrice().multiply(BigDecimal.valueOf(discountRequest.getQuantity()));
        return DiscountDto.builder()
                .type(DiscountType.NONE)
                .status(DiscountStatus.NONE)
                .priceForOne(discountRequest.getPrice())
                .totalCost(cost)
                .quantity(discountRequest.getQuantity())
                .discountPercent(0)
                .discountAbsolute(0)
                .build();
    }

    @Override
    public DiscountDto calculateDiscount(DiscountRequest discountRequest) {
        var handler = firstHandler;
        while (handler != null) {
            var discount = handler.calculateDiscount(discountRequest);
            if (discount != null) {
                return discount;
            }
            handler = handler.next();
        }
        var cost = discountRequest.getPrice().multiply(BigDecimal.valueOf(discountRequest.getQuantity()));
        return DiscountDto.builder()
                .type(DiscountType.NONE)
                .status(DiscountStatus.NONE)
                .priceForOne(discountRequest.getPrice())
                .totalCost(cost)
                .quantity(discountRequest.getQuantity())
                .discountPercent(0)
                .discountAbsolute(0)
                .build();
    }
}
