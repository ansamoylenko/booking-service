package com.samoylenko.bookingservice.service.handler;

import com.samoylenko.bookingservice.model.status.ValidateResult;
import com.samoylenko.bookingservice.service.VoucherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.samoylenko.bookingservice.model.voucher.VoucherType.CERTIFICATE;
import static com.samoylenko.bookingservice.model.voucher.VoucherType.PROMO_CODE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VoucherHandlerUnitTest {
    @Mock
    private VoucherService voucherService;
    @InjectMocks
    private VoucherHandlerFactory voucherHandlerFactory;

    private VoucherHandler voucherHandler;

    @BeforeEach
    public void setup() {
        voucherHandler = voucherHandlerFactory.getVoucherHandler();
    }

    @Test
    public void validate_withPromocode_shouldReturnAppliementResponse() {
        var validateResult = ValidateResult.builder()
                .id("promocodeId")
                .type(PROMO_CODE)
                .status(ValidateResult.Status.VALID)
                .discountPercent(20)
                .discountAbsolute(0)
                .build();
        when(voucherService.validate(anyString(), isNull())).thenReturn(validateResult);
        var voucherRequest = VoucherHandler.AppliementRequest.builder()
                .voucherCode("promocode")
                .quantity(2)
                .price(3000)
                .build();

        var response = voucherHandler.validate(voucherRequest);

        assertThat(response).isNotNull();
        assertThat(response.getVoucherId()).isEqualTo("promocodeId");
        assertThat(response.getQuantity()).isEqualTo(2);
        assertThat(response.getPrice()).isEqualTo(2400);
        assertThat(response.getCost()).isEqualTo(4800);
        assertThat(response.getAppliementResult()).isNotNull();
        assertThat(response.getAppliementResult().getStatus()).isEqualTo(ValidateResult.Status.VALID);
        assertThat(response.getAppliementResult().getDiscountPercent()).isEqualTo(20);
        assertThat(response.getAppliementResult().getDiscountAbsolute()).isEqualTo(0);
    }

    @Test
    public void validate_withCertificate_shouldReturnAppliementResponse() {
        var validateResult = ValidateResult.builder()
                .id("certificateId")
                .type(CERTIFICATE)
                .status(ValidateResult.Status.VALID)
                .discountPercent(0)
                .discountAbsolute(5000)
                .build();
        when(voucherService.validate(anyString(), isNull())).thenReturn(validateResult);
        var voucherRequest = VoucherHandler.AppliementRequest.builder()
                .voucherCode("certificate")
                .quantity(2)
                .price(3000)
                .build();

        var response = voucherHandler.validate(voucherRequest);

        assertThat(response).isNotNull();
        assertThat(response.getVoucherId()).isEqualTo("certificateId");
        assertThat(response.getQuantity()).isEqualTo(2);
        assertThat(response.getPrice()).isEqualTo(500);
        assertThat(response.getCost()).isEqualTo(1000);
        assertThat(response.getAppliementResult()).isNotNull();
        assertThat(response.getAppliementResult().getStatus()).isEqualTo(ValidateResult.Status.VALID);
        assertThat(response.getAppliementResult().getDiscountPercent()).isEqualTo(0);
        assertThat(response.getAppliementResult().getDiscountAbsolute()).isEqualTo(5000);
    }

    @Test
    public void validate_withNotExistVoucher_shouldReturnAppliementResponse() {
        var validateResult = ValidateResult.builder()
                .status(ValidateResult.Status.NOT_VALID)
                .discountPercent(0)
                .discountAbsolute(0)
                .build();
        when(voucherService.validate(anyString(), isNull())).thenReturn(validateResult);
        var voucherRequest = VoucherHandler.AppliementRequest.builder()
                .voucherCode("notExistVoucher")
                .quantity(2)
                .price(3000)
                .build();

        var response = voucherHandler.validate(voucherRequest);

        assertThat(response).isNotNull();
        assertThat(response.getVoucherId()).isNull();
        assertThat(response.getQuantity()).isEqualTo(2);
        assertThat(response.getPrice()).isEqualTo(3000);
        assertThat(response.getCost()).isEqualTo(6000);
        assertThat(response.getAppliementResult()).isNotNull();
        assertThat(response.getAppliementResult().getStatus()).isEqualTo(ValidateResult.Status.NOT_VALID);
        assertThat(response.getAppliementResult().getDiscountPercent()).isEqualTo(0);
        assertThat(response.getAppliementResult().getDiscountAbsolute()).isEqualTo(0);
    }

    @Test
    public void validate_withExpiredPromocode_shouldReturnAppliementResponseWithNotValidStatus() {
        var validateResult = ValidateResult.builder()
                .id("certificateId")
                .type(PROMO_CODE)
                .status(ValidateResult.Status.EXPIRED)
                .discountPercent(0)
                .discountAbsolute(5000)
                .build();
        when(voucherService.validate(anyString(), isNull())).thenReturn(validateResult);
        var voucherRequest = VoucherHandler.AppliementRequest.builder()
                .voucherCode("promocode")
                .quantity(2)
                .price(3000)
                .build();

        var response = voucherHandler.validate(voucherRequest);

        assertThat(response).isNotNull();
        assertThat(response.getVoucherId()).isNull();
        assertThat(response.getQuantity()).isEqualTo(2);
        assertThat(response.getPrice()).isEqualTo(3000);
        assertThat(response.getCost()).isEqualTo(6000);
        assertThat(response.getAppliementResult()).isNotNull();
        assertThat(response.getAppliementResult().getStatus()).isEqualTo(ValidateResult.Status.NOT_VALID);
        assertThat(response.getAppliementResult().getDiscountPercent()).isEqualTo(0);
        assertThat(response.getAppliementResult().getDiscountAbsolute()).isEqualTo(0);
    }


}
