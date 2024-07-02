package com.samoylenko.bookingservice.service.discount;

import com.samoylenko.bookingservice.model.discount.DiscountRequest;
import com.samoylenko.bookingservice.model.discount.DiscountStatus;
import com.samoylenko.bookingservice.model.voucher.VoucherDto;
import com.samoylenko.bookingservice.model.voucher.VoucherStatus;
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
public class PromocodHandlerTest {
    @Mock
    private PromotionService promotionService;
    @InjectMocks
    private PromocodHandler promocodHandler;

    @Test
    public void calculate_withPromocode_shouldReturnAppliementResponse() {
        var voucherDto = VoucherDto.builder()
                .type(PROMO_CODE)
                .status(VoucherStatus.ACTIVE)
                .code("promocode")
                .discountPercent(20)
                .discountAbsolute(0)
                .build();
        when(promotionService.getVoucherByCode(anyString())).thenReturn(voucherDto);
        var voucherRequest = DiscountRequest.builder()
                .code("promocode")
                .quantity(2)
                .price(valueOf(3000))
                .build();

        var response = promocodHandler.calculateDiscount(voucherRequest);

        assertThat(response).isNotNull();
        assertThat(response.getType()).isEqualTo(PROMO_CODE);
        assertThat(response.getStatus()).isEqualTo(DiscountStatus.ACTIVE);
        assertThat(response.getCode()).isEqualTo("promocode");
        assertThat(response.getPriceForOne().compareTo(valueOf(2400))).isEqualTo(0);
        assertThat(response.getTotalCost().compareTo(valueOf(4800))).isEqualTo(0);
        assertThat(response.getQuantity()).isEqualTo(2);
        assertThat(response.getDiscountPercent()).isEqualTo(20);
        assertThat(response.getDiscountAbsolute()).isEqualTo(0);
    }

    @Test
    public void calculate_withCertificate_shouldReturnNull() {
        var voucherDto = VoucherDto.builder()
                .type(CERTIFICATE)
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

        var response = promocodHandler.calculateDiscount(voucherRequest);

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

        var response = promocodHandler.calculateDiscount(voucherRequest);

        assertThat(response).isNull();
    }

    @Test
    public void calculate_withExpiredPromocode_shouldReturnDiscountWithStatusEXPIRED() {
        var voucherDto = VoucherDto.builder()
                .type(PROMO_CODE)
                .status(VoucherStatus.EXPIRED)
                .code("promocode")
                .discountPercent(20)
                .discountAbsolute(0)
                .build();
        when(promotionService.getVoucherByCode(anyString())).thenReturn(voucherDto);
        var voucherRequest = DiscountRequest.builder()
                .code("promocode")
                .quantity(2)
                .price(valueOf(3000))
                .build();

        var response = promocodHandler.calculateDiscount(voucherRequest);

        assertThat(response).isNotNull();
        assertThat(response.getType()).isEqualTo(PROMO_CODE);
        assertThat(response.getStatus()).isEqualTo(DiscountStatus.EXPIRED);
        assertThat(response.getCode()).isEqualTo("promocode");
        assertThat(response.getPriceForOne().compareTo(valueOf(3000))).isEqualTo(0);
        assertThat(response.getTotalCost().compareTo(valueOf(6000))).isEqualTo(0);
        assertThat(response.getQuantity()).isEqualTo(2);
        assertThat(response.getDiscountPercent()).isEqualTo(0);
        assertThat(response.getDiscountAbsolute()).isEqualTo(0);
    }

    @Test
    public void calculate_with100PercentDiscount_shouldReturnDiscountWithZeroCos() {
        var voucherDto = VoucherDto.builder()
                .type(PROMO_CODE)
                .status(VoucherStatus.ACTIVE)
                .code("promocode")
                .discountPercent(100)
                .discountAbsolute(0)
                .build();
        when(promotionService.getVoucherByCode(anyString())).thenReturn(voucherDto);
        var voucherRequest = DiscountRequest.builder()
                .code("promocode")
                .quantity(2)
                .price(valueOf(3000))
                .build();

        var response = promocodHandler.calculateDiscount(voucherRequest);

        assertThat(response).isNotNull();
        assertThat(response.getType()).isEqualTo(PROMO_CODE);
        assertThat(response.getStatus()).isEqualTo(DiscountStatus.ACTIVE);
        assertThat(response.getCode()).isEqualTo("promocode");
        assertThat(response.getPriceForOne().compareTo(valueOf(0))).isEqualTo(0);
        assertThat(response.getTotalCost().compareTo(valueOf(0))).isEqualTo(0);
        assertThat(response.getQuantity()).isEqualTo(2);
        assertThat(response.getDiscountPercent()).isEqualTo(100);
        assertThat(response.getDiscountAbsolute()).isEqualTo(0);
    }
}
