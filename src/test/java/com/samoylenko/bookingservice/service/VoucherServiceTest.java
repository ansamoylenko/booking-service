package com.samoylenko.bookingservice.service;

import com.samoylenko.bookingservice.model.dto.request.VoucherRequest;
import com.samoylenko.bookingservice.model.entity.DefaultVoucherEntityBuilder;
import com.samoylenko.bookingservice.model.status.ValidateResult;
import com.samoylenko.bookingservice.model.status.VoucherStatus;
import com.samoylenko.bookingservice.model.voucher.VoucherCreateDto;
import com.samoylenko.bookingservice.model.voucher.VoucherType;
import com.samoylenko.bookingservice.repository.VoucherRepository;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@AllArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class VoucherServiceTest {

    @Autowired
    private VoucherService voucherService;

    @Autowired
    private VoucherRepository voucherRepository;

    @BeforeEach
    public void setUp() {
        voucherRepository.deleteAll();
    }

    @Test
    public void createPromocode_shouldReturnCreatedVoucher() {
        var createDto = VoucherCreateDto.builder()
                .type(VoucherType.PROMO_CODE)
                .discountAbsolute(300)
                .build();

        var created = voucherService.create(createDto);

        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getType()).isEqualTo(VoucherType.PROMO_CODE);
        assertThat(created.getDiscountAbsolute()).isEqualTo(300);
        assertThat(created.getDiscountPercent()).isEqualTo(0);
        assertThat(created.getStatus()).isEqualTo(VoucherStatus.ACTIVE);
        assertThat(created.getCode()).isNotBlank();
    }

    @Test
    public void validate_withActivePromocode_shouldReturnValidValidateResult() {
        var promocode = voucherRepository.save(DefaultVoucherEntityBuilder.of()
                .withType(VoucherType.PROMO_CODE)
                .withDiscountAbsolute(300)
                .withDiscountPercent(0)
                .build());

        var validated = voucherService.validate(promocode.getCode(), null);

        assertThat(validated).isNotNull();
        assertThat(validated.getStatus()).isEqualTo(ValidateResult.Status.VALID);
        assertThat(validated.getDiscountAbsolute()).isEqualTo(300);
        assertThat(validated.getDiscountPercent()).isEqualTo(0);
    }

    @Test
    public void validate_withExpiredPromocode_shouldReturnExpiredValidateResult() {
        var promocode = voucherRepository.save(DefaultVoucherEntityBuilder.of()
                .withType(VoucherType.PROMO_CODE)
                .withExpiredAt(Instant.now().minusSeconds(1))
                .build());

        var validated = voucherService.validate(promocode.getCode(), null);

        assertThat(validated).isNotNull();
        assertThat(validated.getStatus()).isEqualTo(ValidateResult.Status.EXPIRED);
    }

    @Test
    public void validate_withPromocodeForRoute_shouldReturnNotValidValidateResult() {
        var promocode = voucherRepository.save(DefaultVoucherEntityBuilder.of()
                .withType(VoucherType.PROMO_CODE)
                .withRouteId("testRouteId")
                .build());

        var validated = voucherService.validate(promocode.getCode(), null);

        assertThat(validated).isNotNull();
        assertThat(validated.getStatus()).isEqualTo(ValidateResult.Status.NOT_VALID);
    }

    @Test
    public void validate_withPromocodeForRoute_shouldReturnValidValidateResult() {
        var promocode = voucherRepository.save(DefaultVoucherEntityBuilder.of()
                .withType(VoucherType.PROMO_CODE)
                .withRouteId("testRouteId")
                .build());

        var validated = voucherService.validate(promocode.getCode(), "testRouteId");

        assertThat(validated).isNotNull();
        assertThat(validated.getStatus()).isEqualTo(ValidateResult.Status.VALID);
    }

    @Test
    public void validate_withNotExistPromocode_shouldReturnNotValidValidateResult() {
        var validated = voucherService.validate("notExist", "testRouteId");

        assertThat(validated).isNotNull();
        assertThat(validated.getStatus()).isEqualTo(ValidateResult.Status.NOT_VALID);
    }

    @Test
    public void getVouchers_shouldReturnVouchersOrderByCreatedDate() {
        var promocode1 = voucherRepository.save(DefaultVoucherEntityBuilder.of().withRouteId("testRouteId").build());
        var promocode2 = voucherRepository.save(DefaultVoucherEntityBuilder.of().build());
        var promocode3 = voucherRepository.save(DefaultVoucherEntityBuilder.of().withRouteId("testRouteId").build());
        var promocode4 = voucherRepository.save(DefaultVoucherEntityBuilder.of()
                .withStatus(VoucherStatus.EXPIRED)
                .withRouteId("testRouteId").build());

        var request = VoucherRequest.builder()
                .route("testRouteId")
                .status(VoucherStatus.ACTIVE)
                .build();

        var vouchers = voucherService.getVouchers(request);

        assertThat(vouchers.size()).isEqualTo(2);
        assertThat(vouchers.get(0).getId()).isEqualTo(promocode3.getId());
        assertThat(vouchers.get(1).getId()).isEqualTo(promocode1.getId());
    }

    @Test
    public void getVouchers_withNullArgs_shouldReturnAllVouchers() {
        var promocode1 = voucherRepository.save(DefaultVoucherEntityBuilder.of().build());
        var promocode2 = voucherRepository.save(DefaultVoucherEntityBuilder.of().build());
        var promocode3 = voucherRepository.save(DefaultVoucherEntityBuilder.of().build());
        var request = VoucherRequest.builder().build();

        var vouchers = voucherService.getVouchers(request);

        assertThat(vouchers.size()).isEqualTo(3);
        assertThat(vouchers.get(0).getId()).isEqualTo(promocode3.getId());
        assertThat(vouchers.get(1).getId()).isEqualTo(promocode2.getId());
        assertThat(vouchers.get(2).getId()).isEqualTo(promocode1.getId());
    }

    @Test
    public void apply_shouldReturnValidResult() {
        var promocode = voucherRepository.save(DefaultVoucherEntityBuilder.of()
                .withType(VoucherType.PROMO_CODE)
                .build());

        var result = voucherService.apply(promocode.getCode(), null);

        var found = voucherRepository.findById(promocode.getId());
        assertThat(found).isPresent();
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(ValidateResult.Status.VALID);
        assertThat(found.get().getCount()).isEqualTo(1);
        assertThat(found.get().getStatus()).isEqualTo(VoucherStatus.APPLIED);
    }
}
