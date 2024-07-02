package com.samoylenko.bookingservice.service.discount;

import com.samoylenko.bookingservice.model.discount.DiscountDto;
import com.samoylenko.bookingservice.model.discount.DiscountRequest;
import com.samoylenko.bookingservice.model.discount.DiscountStatus;
import com.samoylenko.bookingservice.model.voucher.VoucherStatus;
import com.samoylenko.bookingservice.service.PromotionService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.samoylenko.bookingservice.model.voucher.DiscountType.PROMO_CODE;
import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static org.springframework.util.StringUtils.hasText;

/**
 * Обработчик для промокодов
 */
@Slf4j
@Getter
@AllArgsConstructor
public class PromocodHandler implements DiscountHandler {
    private final DiscountHandler next;
    private final PromotionService promotionService;

    /**
     * Applies a promotion code to an order and calculates the final cost.
     * <p>
     * Calculation formula:
     * <pre>
     * resultCost = (price - discountAbsolute) * quantity * (100 - discountPercent) / 100
     * resultPrice = resultCost / quantity
     * </pre>
     *
     * @param discountRequest an {@link DiscountRequest} object containing information about the voucher code, price, quantity, and route ID.
     * @return an {@link DiscountDto} object containing the results of applying the voucher code, the new price per unit, the quantity, and the total cost.
     * Returns {@link DiscountDto} with NOT_VALID status  if the voucher code is invalid and there is no next handler in the chain.
     * <p>
     * Examples:
     * <ul>
     *   <li>Price: 100.00, Quantity: 2, Voucher gives a 10% discount:
     *     <pre>
     *     resultCost = (100.00 - 0) * 2 * (100 - 10) / 100 = 180.00
     *     resultPrice = 180.00 / 2 = 90.00
     *     </pre>
     *   </li>
     *   <li>Price: 50.00, Quantity: 3, Voucher gives a 5.00 absolute discount:
     *     <pre>
     *     resultCost = (50.00 - 5.00) * 3 * (100 - 0) / 100 = 135.00
     *     resultPrice = 135.00 / 3 = 45.00
     *     </pre>
     *   </li>
     * </ul>
     */
    @Override
    public DiscountDto calculateDiscount(DiscountRequest discountRequest) {
        if (!hasText(discountRequest.getCode())) return null;
        var voucher = promotionService.getVoucherByCode(discountRequest.getCode());
        if (voucher == null) return null;
        if (!voucher.getType().equals(PROMO_CODE)) return null;
        if (!voucher.getStatus().isValid()) {
            var discountStatus = voucher.getStatus().equals(VoucherStatus.EXPIRED) ?
                    DiscountStatus.EXPIRED :
                    DiscountStatus.ALREADY_APPlIED;
            var cost = discountRequest.getPrice().multiply(BigDecimal.valueOf(discountRequest.getQuantity()));
            return DiscountDto.builder()
                    .type(PROMO_CODE)
                    .code(discountRequest.getCode())
                    .status(discountStatus)
                    .priceForOne(discountRequest.getPrice())
                    .totalCost(cost)
                    .quantity(discountRequest.getQuantity())
                    .build();
        }
        var price = discountRequest.getPrice();
        var quantity = valueOf(discountRequest.getQuantity());
        var discountAbsolute = valueOf(voucher.getDiscountAbsolute());
        var discountPercent = voucher.getDiscountPercent();

        var resultCost = price
                .subtract(discountAbsolute)
                .multiply(quantity)
                .multiply(valueOf(100 - discountPercent))
                .divide(valueOf(100), 2, RoundingMode.UP);
        resultCost = resultCost.compareTo(ZERO) > 0 ? resultCost : ZERO;
        var resultPrice = resultCost.divide(quantity, 2, RoundingMode.UP);
        return DiscountDto.builder()
                .type(PROMO_CODE)
                .code(discountRequest.getCode())
                .status(DiscountStatus.ACTIVE)
                .discountPercent(voucher.getDiscountPercent())
                .discountAbsolute(voucher.getDiscountAbsolute())
                .quantity(discountRequest.getQuantity())
                .priceForOne(resultPrice)
                .totalCost(resultCost)
                .build();
    }

    @Override
    public DiscountHandler next() {
        return next;
    }

    @Override
    public DiscountDto applyDiscount(DiscountRequest request) {
        var discount = calculateDiscount(request);
        if (discount == null) return null;
        if (discount.getStatus().isValid()) {
            log.info("Applying discount: {}", discount);
            promotionService.applyVoucher(request);
        }
        return discount;
    }
}
