package com.samoylenko.bookingservice.service.handler;

import com.samoylenko.bookingservice.model.status.ValidateResult;
import com.samoylenko.bookingservice.service.VoucherService;

import java.math.RoundingMode;

import static com.samoylenko.bookingservice.model.voucher.VoucherType.CERTIFICATE;
import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;

public class CertificateHandler extends VoucherHandler {
    public CertificateHandler(VoucherHandler next, VoucherService voucherService) {
        super(next, voucherService, CERTIFICATE);
    }


    /**
     * Applies a voucher code (certificate) to an order and calculates the final cost.
     * <p>
     * Calculation formula for certificates:
     * <pre>
     * resultCost = (price * quantity) - discountAbsolute
     * resultPrice = resultCost / quantity
     * </pre>
     *
     * @param request an {@link AppliementRequest} object containing information about the voucher code, price, quantity, and route ID.
     * @return an {@link AppliementResponse} object containing the results of applying the voucher code, the new price per unit, the quantity, and the total cost.
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
    protected AppliementResponse calculate(ValidateResult result, AppliementRequest request) {
        var price = valueOf(request.getPrice());
        var quantity = valueOf(request.getQuantity());
        var discountAbsolute = result.getDiscountAbsolute();

        var resultCost = price
                .multiply(quantity)
                .subtract(valueOf(discountAbsolute));
        resultCost = resultCost.compareTo(ZERO) > 0 ? resultCost : ZERO;

        var resultPrice = resultCost.divide(quantity, 2, RoundingMode.UP);
        return AppliementResponse.builder()
                .appliementResult(result)
                .voucherId(result.getId())
                .price(resultPrice.intValue())
                .quantity(quantity.intValue())
                .cost(resultCost.intValue())
                .build();
    }
}
