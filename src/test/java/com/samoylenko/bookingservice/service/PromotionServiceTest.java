package com.samoylenko.bookingservice.service;

import com.samoylenko.bookingservice.model.discount.DiscountRequest;
import com.samoylenko.bookingservice.model.entity.DefaultVoucherEntityBuilder;
import com.samoylenko.bookingservice.model.exception.EntityCreateException;
import com.samoylenko.bookingservice.model.exception.EntityNotFoundException;
import com.samoylenko.bookingservice.model.voucher.DiscountType;
import com.samoylenko.bookingservice.model.voucher.VoucherCreateDto;
import com.samoylenko.bookingservice.model.voucher.VoucherRequest;
import com.samoylenko.bookingservice.model.voucher.VoucherStatus;
import com.samoylenko.bookingservice.repository.VoucherRepository;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import static com.samoylenko.bookingservice.model.voucher.DiscountType.CERTIFICATE;
import static com.samoylenko.bookingservice.model.voucher.DiscountType.PROMO_CODE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@AllArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class PromotionServiceTest {

    @Autowired
    private PromotionService promotionService;

    @Autowired
    private VoucherRepository voucherRepository;

    @BeforeEach
    public void setUp() {
        voucherRepository.deleteAll();
    }

    @Test
    public void createVoucher_shouldReturnCreatedVoucher() {
        var createDto = VoucherCreateDto.builder()
                .type(DiscountType.PROMO_CODE)
                .discountAbsolute(300)
                .build();

        var created = promotionService.createVoucher(createDto);

        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getType()).isEqualTo(DiscountType.PROMO_CODE);
        assertThat(created.getDiscountAbsolute()).isEqualTo(300);
        assertThat(created.getDiscountPercent()).isEqualTo(0);
        assertThat(created.getStatus()).isEqualTo(VoucherStatus.ACTIVE);
        assertThat(created.getCode()).isNotBlank();
    }

    @Test
    public void createVoucher_withNotUnique_shouldReturnException() {
        var createDto = VoucherCreateDto.builder()
                .type(DiscountType.PROMO_CODE)
                .code("code")
                .discountAbsolute(300)
                .build();

        var created = promotionService.createVoucher(createDto);

        assertThat(created).isNotNull();
        assertThatThrownBy(() -> promotionService.createVoucher(createDto))
                .isInstanceOf(EntityCreateException.class);
    }

    @Test
    public void getVoucherByCode_withActivePromocode_shouldReturnVoucherDto() {
        var promocode = voucherRepository.save(DefaultVoucherEntityBuilder.of()
                .withType(DiscountType.PROMO_CODE)
                .withDiscountAbsolute(300)
                .withDiscountPercent(0)
                .build());

        var voucher = promotionService.getVoucherByCode(promocode.getCode());

        assertThat(voucher).isNotNull();
        assertThat(voucher.getStatus()).isEqualTo(VoucherStatus.ACTIVE);
        assertThat(voucher.getDiscountAbsolute()).isEqualTo(300);
        assertThat(voucher.getDiscountPercent()).isEqualTo(0);
    }

    @Test
    public void getVoucherByCode_withNullArg_shouldReturnNull() {
        var voucher = promotionService.getVoucherByCode(null);

        assertThat(voucher).isNull();
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

        var vouchers = promotionService.getVouchers(request);

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

        var vouchers = promotionService.getVouchers(request);

        assertThat(vouchers.size()).isEqualTo(3);
        assertThat(vouchers.get(0).getId()).isEqualTo(promocode3.getId());
        assertThat(vouchers.get(1).getId()).isEqualTo(promocode2.getId());
        assertThat(vouchers.get(2).getId()).isEqualTo(promocode1.getId());
    }

    @Test
    public void applyVoucher_withCertificate_shouldUpdateVoucher() {
        var certificate = voucherRepository.save(DefaultVoucherEntityBuilder.of()
                .withType(CERTIFICATE)
                .build());
        var request = DiscountRequest.builder()
                .code(certificate.getCode())
                .build();

        promotionService.applyVoucher(request);

        var appliedCertificate = voucherRepository.findById(certificate.getId());
        assertThat(appliedCertificate).isPresent();
        assertThat(appliedCertificate.get().getStatus()).isEqualTo(VoucherStatus.APPLIED);
        assertThat(appliedCertificate.get().getCount()).isEqualTo(1);
    }

    @Test
    public void applyVoucher_withPromocode_shouldUpdateVoucher() {
        var certificate = voucherRepository.save(DefaultVoucherEntityBuilder.of()
                .withType(PROMO_CODE)
                .build());
        var request = DiscountRequest.builder()
                .code(certificate.getCode())
                .build();

        promotionService.applyVoucher(request);

        var appliedCertificate = voucherRepository.findById(certificate.getId());
        assertThat(appliedCertificate).isPresent();
        assertThat(appliedCertificate.get().getStatus()).isEqualTo(VoucherStatus.ACTIVE);
        assertThat(appliedCertificate.get().getCount()).isEqualTo(1);
    }

    @Test
    public void applyVoucher_withNotExistPromocode_shouldUpdateVoucher() {
        var request = DiscountRequest.builder()
                .code("notExistCode")
                .build();

        assertThatThrownBy(() -> promotionService.applyVoucher(request))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
