package com.samoylenko.bookingservice.service.discount;

import com.samoylenko.bookingservice.model.promotion.DiscountRequest;
import com.samoylenko.bookingservice.model.promotion.DiscountStatus;
import com.samoylenko.bookingservice.model.promotion.VoucherStatus;
import com.samoylenko.bookingservice.model.voucher.VoucherDto;
import com.samoylenko.bookingservice.service.PromotionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.samoylenko.bookingservice.model.voucher.DiscountType.CERTIFICATE;
import static com.samoylenko.bookingservice.model.voucher.DiscountType.PROMO_CODE;
import static java.math.BigDecimal.valueOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CertificateHandlerTest {
    @Mock
    private PromotionService promotionService;
    @InjectMocks
    private CertificateHandler certificateHandler;

    @Test
    public void calculate_withPromocode_shouldReturnAppliementResponse() {
        var voucherDto = VoucherDto.builder()
                .type(CERTIFICATE)
                .status(VoucherStatus.ACTIVE)
                .code("certificate")
                .discountPercent(0)
                .discountAbsolute(3500)
                .build();
        when(promotionService.getVoucherByCode(anyString())).thenReturn(voucherDto);
        var voucherRequest = DiscountRequest.builder()
                .code("certificate")
                .quantity(2)
                .price(valueOf(3000))
                .build();

        var response = certificateHandler.calculateDiscount(voucherRequest);

        assertThat(response).isNotNull();
        assertThat(response.getType()).isEqualTo(CERTIFICATE);
        assertThat(response.getStatus()).isEqualTo(DiscountStatus.ACTIVE);
        assertThat(response.getCode()).isEqualTo("certificate");
        assertThat(response.getPriceForOne().compareTo(valueOf(1250))).isEqualTo(0);
        assertThat(response.getTotalCost().compareTo(valueOf(2500))).isEqualTo(0);
        assertThat(response.getQuantity()).isEqualTo(2);
        assertThat(response.getDiscountPercent()).isEqualTo(0);
        assertThat(response.getDiscountAbsolute()).isEqualTo(3500);
    }

    @Test
    public void calculate_withPromocode_shouldReturnNull() {
        var voucherDto = VoucherDto.builder()
                .type(PROMO_CODE)
                .status(VoucherStatus.ACTIVE)
                .code("code")
                .discountPercent(20)
                .discountAbsolute(0)
                .build();
        when(promotionService.getVoucherByCode(anyString())).thenReturn(voucherDto);
        var voucherRequest = DiscountRequest.builder()
                .code("code")
                .quantity(2)
                .price(valueOf(3000))
                .build();

        var response = certificateHandler.calculateDiscount(voucherRequest);

        assertThat(response).isNull();
    }

    @Test
    public void calculate_withNullDiscount_shouldReturnNull() {
        when(promotionService.getVoucherByCode(anyString())).thenReturn(null);
        var voucherRequest = DiscountRequest.builder()
                .code("promocode")
                .quantity(2)
                .price(valueOf(3000))
                .build();

        var response = certificateHandler.calculateDiscount(voucherRequest);

        assertThat(response).isNull();
    }

    @Test
    public void calculate_withExpiredCertificate_shouldReturnDiscountWithStatusEXPIRED() {
        var voucherDto = VoucherDto.builder()
                .type(CERTIFICATE)
                .status(VoucherStatus.EXPIRED)
                .code("certificate")
                .discountPercent(20)
                .discountAbsolute(0)
                .build();
        when(promotionService.getVoucherByCode(anyString())).thenReturn(voucherDto);
        var voucherRequest = DiscountRequest.builder()
                .code("certificate")
                .quantity(2)
                .price(valueOf(3000))
                .build();

        var response = certificateHandler.calculateDiscount(voucherRequest);

        assertThat(response).isNotNull();
        assertThat(response.getType()).isEqualTo(CERTIFICATE);
        assertThat(response.getStatus()).isEqualTo(DiscountStatus.EXPIRED);
        assertThat(response.getCode()).isEqualTo("certificate");
        assertThat(response.getPriceForOne().compareTo(valueOf(3000))).isEqualTo(0);
        assertThat(response.getTotalCost().compareTo(valueOf(6000))).isEqualTo(0);
        assertThat(response.getQuantity()).isEqualTo(2);
        assertThat(response.getDiscountPercent()).isEqualTo(0);
        assertThat(response.getDiscountAbsolute()).isEqualTo(0);
    }

    @Test
    public void calculate_withBigAbsoluteDiscount_shouldReturnDiscountWithZeroCos() {
        var voucherDto = VoucherDto.builder()
                .type(CERTIFICATE)
                .status(VoucherStatus.ACTIVE)
                .code("certificate")
                .discountPercent(0)
                .discountAbsolute(10000)
                .build();
        when(promotionService.getVoucherByCode(anyString())).thenReturn(voucherDto);
        var voucherRequest = DiscountRequest.builder()
                .code("certificate")
                .quantity(2)
                .price(valueOf(3000))
                .build();

        var response = certificateHandler.calculateDiscount(voucherRequest);

        assertThat(response).isNotNull();
        assertThat(response.getType()).isEqualTo(CERTIFICATE);
        assertThat(response.getStatus()).isEqualTo(DiscountStatus.ACTIVE);
        assertThat(response.getCode()).isEqualTo("certificate");
        assertThat(response.getPriceForOne().compareTo(valueOf(0))).isEqualTo(0);
        assertThat(response.getTotalCost().compareTo(valueOf(0))).isEqualTo(0);
        assertThat(response.getQuantity()).isEqualTo(2);
        assertThat(response.getDiscountPercent()).isEqualTo(0);
        assertThat(response.getDiscountAbsolute()).isEqualTo(10000);
    }
}
