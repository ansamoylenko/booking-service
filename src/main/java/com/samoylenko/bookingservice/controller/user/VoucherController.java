package com.samoylenko.bookingservice.controller.user;

import com.samoylenko.bookingservice.service.WalkService;
import com.samoylenko.bookingservice.service.handler.VoucherHandler;
import com.samoylenko.bookingservice.service.handler.VoucherHandlerFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vouchers")
@Tag(name = "Промокоды и сертификаты (для пользователя)")
@AllArgsConstructor
public class VoucherController {
    private final VoucherHandlerFactory voucherHandlerFactory;
    private final WalkService walkService;

    @Operation(summary = "Провалидировать промокод")
    @GetMapping("/validate")
    @ResponseStatus(HttpStatus.OK)
    public VoucherHandler.AppliementResponse validateVoucher(
            @RequestParam String code,
            @RequestParam(value = "walk", required = false) String walkId,
            @RequestParam Integer quantity
    ) {
        var walk = walkService.getWalkForUser(walkId);
        var request = VoucherHandler.AppliementRequest.builder()
                .voucherCode(code)
                .routeId(walk.getRoute().getId())
                .quantity(quantity)
                .price(walk.getPriceForOne())
                .build();
        return voucherHandlerFactory.getVoucherHandler().validate(request);
    }
}
