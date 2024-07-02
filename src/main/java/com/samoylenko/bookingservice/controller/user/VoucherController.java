package com.samoylenko.bookingservice.controller.user;

import com.samoylenko.bookingservice.model.discount.DiscountDto;
import com.samoylenko.bookingservice.model.discount.DiscountRequest;
import com.samoylenko.bookingservice.service.WalkService;
import com.samoylenko.bookingservice.service.discount.DiscountManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/vouchers")
@Tag(name = "Промокоды и сертификаты (для пользователя)")
@AllArgsConstructor
public class VoucherController {
    private final DiscountManager discountManager;
    private final WalkService walkService;

    @Operation(summary = "Получить итоговую стоймость бронирования")
    @GetMapping("/calculate")
    @ResponseStatus(HttpStatus.OK)
    public DiscountDto calculate(
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "walk", required = false) String walkId,
            @RequestParam(value = "quantity", required = false) Integer quantity,
            @RequestParam(value = "phone", required = false) String phone
    ) {
        var walk = walkService.getWalkForUser(walkId);
        var request = DiscountRequest.builder()
                .code(code)
                .routeId(walk.getRoute().getId())
                .quantity(quantity)
                .price(BigDecimal.valueOf(walk.getPriceForOne()))
                .phone(phone)
                .build();
        return discountManager.calculateDiscount(request);
    }
}
