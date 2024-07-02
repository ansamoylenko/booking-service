package com.samoylenko.bookingservice.service.discount;

import com.samoylenko.bookingservice.config.ServiceProperties;
import com.samoylenko.bookingservice.model.dto.request.BookingRequest;
import com.samoylenko.bookingservice.model.promotion.DiscountRequest;
import com.samoylenko.bookingservice.model.promotion.DiscountStatus;
import com.samoylenko.bookingservice.service.BookingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import static com.samoylenko.bookingservice.model.voucher.DiscountType.REPEATED_BOOKING;
import static java.math.BigDecimal.valueOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RepeatedBookingDiscountHandlerTest {
    @Mock
    private ServiceProperties serviceProperties;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private RepeatedBookingDiscountHandler handler;

    @Test
    public void calculate_withOneBooking_shouldReturnCalculateDiscount() {
        when(serviceProperties.isRepeatedBookingDiscountEnabled()).thenReturn(true);
        when(serviceProperties.getRepeatedBookingDiscountPercent()).thenReturn(15);
        when(serviceProperties.getRepeatedBookingDiscountAbsolute()).thenReturn(0);
        var voucherRequest = DiscountRequest.builder()
                .phone("testPhone")
                .quantity(5)
                .price(valueOf(3000))
                .build();
        Page mockPage = mock(Page.class);
        when(mockPage.getTotalElements()).thenReturn(1L);
        when(bookingService.getBookings(any(BookingRequest.class))).thenReturn(mockPage);

        var response = handler.calculateDiscount(voucherRequest);

        assertThat(response).isNotNull();
        assertThat(response.getType()).isEqualTo(REPEATED_BOOKING);
        assertThat(response.getStatus()).isEqualTo(DiscountStatus.ACTIVE);
        assertThat(response.getPriceForOne().compareTo(valueOf(2550))).isEqualTo(0);
        assertThat(response.getTotalCost().compareTo(valueOf(12750))).isEqualTo(0);
        assertThat(response.getQuantity()).isEqualTo(5);
        assertThat(response.getDiscountPercent()).isEqualTo(15);
        assertThat(response.getDiscountAbsolute()).isEqualTo(0);
    }

    @Test
    public void calculate_withoutBooking_shouldReturnNull() {
        when(serviceProperties.isRepeatedBookingDiscountEnabled()).thenReturn(true);
        var voucherRequest = DiscountRequest.builder()
                .phone("testPhone")
                .quantity(5)
                .price(valueOf(3000))
                .build();
        Page mockPage = mock(Page.class);
        when(mockPage.getTotalElements()).thenReturn(0L);
        when(bookingService.getBookings(any(BookingRequest.class))).thenReturn(mockPage);

        var response = handler.calculateDiscount(voucherRequest);

        assertThat(response).isNull();
    }
}

