package com.samoylenko.bookingservice.service.handler;

import com.samoylenko.bookingservice.model.status.ValidateResult;
import com.samoylenko.bookingservice.model.voucher.VoucherType;
import com.samoylenko.bookingservice.service.VoucherService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;

import static com.samoylenko.bookingservice.model.status.ValidateResult.Status.NOT_VALID;
import static com.samoylenko.bookingservice.model.status.ValidateResult.Status.VALID;

@AllArgsConstructor
public abstract class VoucherHandler {
    protected VoucherHandler next;
    protected VoucherService voucherService;
    private VoucherType targetType;

    public AppliementResponse apply(AppliementRequest request) {
        var result = voucherService.validate(request.getVoucherCode(), request.getRouteId());

        if (result.getStatus().equals(VALID) && result.getType().equals(targetType)) {
            var response = calculate(result, request);
            voucherService.apply(request.getVoucherCode(), request.getRouteId());
            return response;
        } else if (next != null) {
            return next.apply(request);
        } else return AppliementResponse.builder()
                .appliementResult(ValidateResult.builder().status(NOT_VALID).build())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .cost(request.getPrice() * request.getQuantity())
                .build();
    }

    public AppliementResponse validate(AppliementRequest request) {
        var result = voucherService.validate(request.getVoucherCode(), request.getRouteId());
        if (result.getStatus().equals(VALID) && result.getType().equals(targetType)) {
            return calculate(result, request);
        } else if (next != null) {
            return next.validate(request);
        } else return AppliementResponse.builder()
                .appliementResult(ValidateResult.builder().status(NOT_VALID).build())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .cost(request.getPrice() * request.getQuantity())
                .build();
    }

    protected abstract AppliementResponse calculate(ValidateResult result, AppliementRequest request);


    @Getter
    @Builder
    @Generated
    public static class AppliementRequest {
        private String voucherCode;
        private int price;
        private int quantity;
        private String routeId;
    }

    @Getter
    @Builder
    @Generated
    public static class AppliementResponse {
        private String voucherId;
        private ValidateResult appliementResult;
        private int price;
        private int quantity;
        private int cost;
    }
}
