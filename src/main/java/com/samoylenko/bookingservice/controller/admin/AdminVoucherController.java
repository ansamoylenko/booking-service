package com.samoylenko.bookingservice.controller.admin;

import com.samoylenko.bookingservice.model.voucher.VoucherCreateDto;
import com.samoylenko.bookingservice.model.voucher.VoucherDto;
import com.samoylenko.bookingservice.model.voucher.VoucherRequest;
import com.samoylenko.bookingservice.model.voucher.VoucherStatus;
import com.samoylenko.bookingservice.service.PromotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/vouchers")
@Tag(name = "Промокоды и сертификаты (для администратора)")
@AllArgsConstructor
public class AdminVoucherController {
    private final PromotionService promotionService;

    @Operation(summary = "Создание промокода")
    @PostMapping
    public ResponseEntity<VoucherDto> createVoucher(@RequestBody VoucherCreateDto dto, UriComponentsBuilder uriBuilder) {
        VoucherDto voucher = promotionService.createVoucher(dto);
        var uri = uriBuilder.path("/api/v1/admin/vouchers/{id}")
                .buildAndExpand(voucher.getId())
                .toUri();
        return ResponseEntity.created(uri).body(voucher);
    }

    @Operation(summary = "Получить промокод")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public VoucherDto getVoucher(@PathVariable String id) {
        return promotionService.getVoucherById(id);
    }

    @Operation(summary = "Получить список промокодов")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<VoucherDto> getVouchers(
            @RequestParam(value = "route", required = false) String route,
            @RequestParam(value = "status", required = false) VoucherStatus status
    ) {
        var request = VoucherRequest.builder()
                .route(route)
                .status(status)
                .build();
        return promotionService.getVouchers(request);
    }
}
