package com.samoylenko.bookingservice.service.handler;

import com.samoylenko.bookingservice.model.status.ValidateResult;
import com.samoylenko.bookingservice.service.VoucherService;

import java.math.RoundingMode;

import static com.samoylenko.bookingservice.model.voucher.VoucherType.PROMO_CODE;
import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;

public class PromocodHandler extends VoucherHandler {
    public PromocodHandler(VoucherHandler next, VoucherService voucherService) {
        super(next, voucherService, PROMO_CODE);
    }

    /**
     * Applies a promotion code to an order and calculates the final cost.
     * <p>
     * Calculation formula:
     * <pre>
     * resultCost = (price - discountAbsolute) * quantity * (100 - discountPercent) / 100
     * resultPrice = resultCost / quantity
     * </pre>
     *
     * @param request an {@link AppliementRequest} object containing information about the voucher code, price, quantity, and route ID.
     * @return an {@link AppliementResponse} object containing the results of applying the voucher code, the new price per unit, the quantity, and the total cost.
     * Returns {@link ValidateResult} with NOT_VALID status  if the voucher code is invalid and there is no next handler in the chain.
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
    protected AppliementResponse calculate(ValidateResult result, AppliementRequest request) {
        var price = request.getPrice();
        var quantity = request.getQuantity();
        var discountPercent = result.getDiscountPercent();
        var discountAbsolute = result.getDiscountAbsolute();

        var resultCost = valueOf(price)
                .subtract(valueOf(discountAbsolute))
                .multiply(valueOf(quantity))
                .multiply(valueOf(100 - discountPercent))
                .divide(valueOf(100), 2, RoundingMode.UP);
        resultCost = resultCost.compareTo(ZERO) > 0 ? resultCost : ZERO;

        var resultPrice = resultCost.divide(valueOf(quantity), 2, RoundingMode.UP);
        return AppliementResponse.builder()
                .appliementResult(result)
                .voucherId(result.getId())
                .price(resultPrice.intValue())
                .quantity(quantity)
                .cost(resultCost.intValue())
                .build();
    }
}
