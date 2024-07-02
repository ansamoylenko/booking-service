package com.samoylenko.bookingservice.service.discount;

import com.samoylenko.bookingservice.model.promotion.DiscountDto;
import com.samoylenko.bookingservice.model.promotion.DiscountRequest;
import com.samoylenko.bookingservice.model.promotion.DiscountStatus;
import com.samoylenko.bookingservice.model.promotion.VoucherStatus;
import com.samoylenko.bookingservice.service.PromotionService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.math.RoundingMode;

import static com.samoylenko.bookingservice.model.voucher.DiscountType.CERTIFICATE;
import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static org.springframework.util.StringUtils.hasText;

/**
 * Обработчик для сертификатов
 */
@Slf4j
@Getter
@AllArgsConstructor
public class CertificateHandler implements DiscountHandler {
    private final DiscountHandler next;
    private final PromotionService promotionService;


    /**
     * Applies a voucher code (certificate) to an order and calculates the final cost.
     * <p>
     * Calculation formula for certificates:
     * <pre>
     * resultCost = (price * quantity) - discountAbsolute
     * resultPrice = resultCost / quantity
     * </pre>
     *
     * @param discountRequest an {@link DiscountRequest} object containing information about the voucher code, price, quantity, and route ID.
     * @return an {@link DiscountDto} object containing the results of applying the voucher code, the new price per unit, the quantity, and the total cost.
     * If the voucher code is invalid and there is no next handler in the chain, returns a response with a status of NOT_VALID.
     * <p>
     * Examples:
     * <ul>
     *   <li>Price: 100.00, Quantity: 2, Voucher gives a 20.00 absolute discount:
     *     <pre>
     *     resultCost = (100.00 * 2) - 20.00 = 180.00
     *     resultPrice = 180.00 / 2 = 90.00
     *     </pre>
     *   </li>
     *   <li>Price: 3000.00, Quantity: 3, Voucher gives a 10000.00 absolute discount:
     *     <pre>
     *     resultCost = (3000.00 * 3) - 10000.00 = -1000.00 => resultCost = 0
     *     </pre>
     *   </li>
     * </ul>
     */
    @Override
    public DiscountDto calculateDiscount(DiscountRequest discountRequest) {
        if (!hasText(discountRequest.getCode())) return null;
        var voucher = promotionService.getVoucherByCode(discountRequest.getCode());
        if (voucher == null) return null;
        if (!voucher.getType().equals(CERTIFICATE)) return null;
        if (!voucher.getStatus().isValid()) {
            var discountStatus = voucher.getStatus().equals(VoucherStatus.EXPIRED) ?
                    DiscountStatus.EXPIRED :
                    DiscountStatus.ALREADY_APPlIED;
            var cost = discountRequest.getPrice().multiply(valueOf(discountRequest.getQuantity()));
            return DiscountDto.builder()
                    .type(CERTIFICATE)
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
        var resultCost = price.multiply(quantity).subtract(discountAbsolute);
        resultCost = resultCost.compareTo(ZERO) > 0 ? resultCost : ZERO;
        var resultPrice = resultCost.divide(quantity, 2, RoundingMode.UP);
        return DiscountDto.builder()
                .type(CERTIFICATE)
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
