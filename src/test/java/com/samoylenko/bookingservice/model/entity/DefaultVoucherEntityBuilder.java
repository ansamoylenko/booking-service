package com.samoylenko.bookingservice.model.entity;

import com.samoylenko.bookingservice.model.voucher.DiscountType;
import com.samoylenko.bookingservice.model.voucher.VoucherEntity;
import com.samoylenko.bookingservice.model.voucher.VoucherStatus;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.time.Instant;
import java.util.Random;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "of")
public class DefaultVoucherEntityBuilder implements DefaultEntityBuilder<VoucherEntity> {
    private DiscountType type = DiscountType.PROMO_CODE;
    private VoucherStatus status = VoucherStatus.ACTIVE;
    private String code = String.valueOf(new Random().nextInt(100000));
    private Integer discountPercent = 0;
    private Integer discountAbsolute = 0;
    private Integer count = 0;
    private String distributor;
    private String routeId;
    private Instant expiredAt;

    @Override
    public VoucherEntity build() {
        return VoucherEntity.builder()
                .type(type)
                .status(status)
                .code(code)
                .discountPercent(discountPercent)
                .discountAbsolute(discountAbsolute)
                .count(count)
                .distributor(distributor)
                .routeId(routeId)
                .expiredAt(expiredAt)
                .build();
    }
}
