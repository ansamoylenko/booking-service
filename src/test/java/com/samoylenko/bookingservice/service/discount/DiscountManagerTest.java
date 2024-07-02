package com.samoylenko.bookingservice.service.discount;

import com.samoylenko.bookingservice.model.booking.BookingRequest;
import com.samoylenko.bookingservice.model.discount.DiscountRequest;
import com.samoylenko.bookingservice.model.discount.DiscountStatus;
import com.samoylenko.bookingservice.model.voucher.DiscountType;
import com.samoylenko.bookingservice.model.voucher.VoucherDto;
import com.samoylenko.bookingservice.model.voucher.VoucherStatus;
import com.samoylenko.bookingservice.service.BookingService;
import com.samoylenko.bookingservice.service.PromotionService;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import static java.math.BigDecimal.valueOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@AllArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class DiscountManagerTest {
    private final DiscountManager discountManager;

    @MockBean
    private PromotionService promotionService;

    @MockBean
    private BookingService bookingService;

    @Test
    public void calculateDiscount_shouldReturnDiscount() {
        Page mockPage = mock(Page.class);
        when(mockPage.getTotalElements()).thenReturn(0L);
        when(bookingService.getBookings(any(BookingRequest.class))).thenReturn(mockPage);
        var discountRequest = DiscountRequest.builder()
                .quantity(2)
                .price(valueOf(1000))
                .build();

        var discount = discountManager.calculateDiscount(discountRequest);

        assertThat(discount).isNotNull();
        assertThat(discount.getType()).isEqualTo(DiscountType.NONE);
        assertThat(discount.getStatus()).isEqualTo(DiscountStatus.NONE);
    }

    @Test
    public void calculateDiscount_withPromocode_shouldReturnDiscount() {
        var mockVoucher = VoucherDto.builder()
                .code("promocode")
                .type(DiscountType.PROMO_CODE)
                .status(VoucherStatus.ACTIVE)
                .discountPercent(15)
                .discountAbsolute(0)
                .build();
        when(promotionService.getVoucherByCode(anyString())).thenReturn(mockVoucher);
        var discountRequest = DiscountRequest.builder()
                .code("promocode")
                .quantity(2)
                .price(valueOf(1000))
                .build();

        var discount = discountManager.calculateDiscount(discountRequest);

        assertThat(discount).isNotNull();
        assertThat(discount.getType()).isEqualTo(DiscountType.PROMO_CODE);
        assertThat(discount.getStatus()).isEqualTo(DiscountStatus.ACTIVE);
        assertThat(discount.getDiscountPercent()).isEqualTo(15);
        assertThat(discount.getDiscountAbsolute()).isEqualTo(0);
        assertThat(discount.getPriceForOne().compareTo(valueOf(850))).isEqualTo(0);
        assertThat(discount.getTotalCost().compareTo(valueOf(1700))).isEqualTo(0);
    }

    @Test
    public void calculateDiscount_withCertificate_shouldReturnDiscount() {
        var mockVoucher = VoucherDto.builder()
                .code("certificate")
                .type(DiscountType.CERTIFICATE)
                .status(VoucherStatus.ACTIVE)
                .discountPercent(0)
                .discountAbsolute(1000)
                .build();
        when(promotionService.getVoucherByCode(anyString())).thenReturn(mockVoucher);
        var discountRequest = DiscountRequest.builder()
                .code("certificate")
                .quantity(5)
                .price(valueOf(1000))
                .build();

        var discount = discountManager.calculateDiscount(discountRequest);

        assertThat(discount).isNotNull();
        assertThat(discount.getType()).isEqualTo(DiscountType.CERTIFICATE);
        assertThat(discount.getStatus()).isEqualTo(DiscountStatus.ACTIVE);
        assertThat(discount.getDiscountPercent()).isEqualTo(0);
        assertThat(discount.getDiscountAbsolute()).isEqualTo(1000);
        assertThat(discount.getPriceForOne().compareTo(valueOf(800))).isEqualTo(0);
        assertThat(discount.getTotalCost().compareTo(valueOf(4000))).isEqualTo(0);
    }


    @Test
    public void calculateDiscount_forGroupBooking_shouldReturnDiscount() {
        var discountRequest = DiscountRequest.builder()
                .quantity(5)
                .price(valueOf(1000))
                .build();

        var discount = discountManager.calculateDiscount(discountRequest);

        assertThat(discount).isNotNull();
        assertThat(discount.getType()).isEqualTo(DiscountType.GROUP_BOOKING);
        assertThat(discount.getStatus()).isEqualTo(DiscountStatus.ACTIVE);
        assertThat(discount.getDiscountPercent()).isEqualTo(15);
        assertThat(discount.getDiscountAbsolute()).isEqualTo(0);
        assertThat(discount.getPriceForOne().compareTo(valueOf(850))).isEqualTo(0);
        assertThat(discount.getTotalCost().compareTo(valueOf(4250))).isEqualTo(0);
    }

    @Test
    public void calculateDiscount_forSecondBooking_shouldReturnDiscount() {
        Page mockPage = mock(Page.class);
        when(mockPage.getTotalElements()).thenReturn(1L);
        when(bookingService.getBookings(any(BookingRequest.class))).thenReturn(mockPage);
        var discountRequest = DiscountRequest.builder()
                .quantity(2)
                .price(valueOf(1000))
                .build();

        var discount = discountManager.calculateDiscount(discountRequest);

        assertThat(discount).isNotNull();
        assertThat(discount.getType()).isEqualTo(DiscountType.REPEATED_BOOKING);
        assertThat(discount.getStatus()).isEqualTo(DiscountStatus.ACTIVE);
        assertThat(discount.getDiscountPercent()).isEqualTo(15);
        assertThat(discount.getDiscountAbsolute()).isEqualTo(0);
        assertThat(discount.getPriceForOne().compareTo(valueOf(850))).isEqualTo(0);
        assertThat(discount.getTotalCost().compareTo(valueOf(1700))).isEqualTo(0);
    }

    @Test
    public void calculateDiscount_with5placesAndSecondBooking_shouldReturnDiscount() {
        Page mockPage = mock(Page.class);
        when(mockPage.getTotalElements()).thenReturn(1L);
        when(bookingService.getBookings(any(BookingRequest.class))).thenReturn(mockPage);
        var discountRequest = DiscountRequest.builder()
                .quantity(5)
                .price(valueOf(1000))
                .build();

        var discount = discountManager.calculateDiscount(discountRequest);

        assertThat(discount).isNotNull();
        assertThat(discount.getType()).isEqualTo(DiscountType.GROUP_BOOKING);
        assertThat(discount.getStatus()).isEqualTo(DiscountStatus.ACTIVE);
        assertThat(discount.getDiscountPercent()).isEqualTo(15);
        assertThat(discount.getDiscountAbsolute()).isEqualTo(0);
        assertThat(discount.getPriceForOne().compareTo(valueOf(850))).isEqualTo(0);
        assertThat(discount.getTotalCost().compareTo(valueOf(4250))).isEqualTo(0);
    }
}
