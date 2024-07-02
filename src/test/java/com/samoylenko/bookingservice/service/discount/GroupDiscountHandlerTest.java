package com.samoylenko.bookingservice.service.discount;

import com.samoylenko.bookingservice.config.ServiceProperties;
import com.samoylenko.bookingservice.model.promotion.DiscountRequest;
import com.samoylenko.bookingservice.model.promotion.DiscountStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.samoylenko.bookingservice.model.voucher.DiscountType.GROUP_BOOKING;
import static java.math.BigDecimal.valueOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GroupDiscountHandlerTest {
    @Mock
    private ServiceProperties serviceProperties;
    @InjectMocks
    private GroupDiscountHandler groupDiscountHandler;

    @Test
    public void calculate_forFivePlaces_shouldReturnAppliementResponse() {
        when(serviceProperties.isGroupDiscountEnabled()).thenReturn(true);
        when(serviceProperties.getGroupDiscountMinPlaces()).thenReturn(5);
        when(serviceProperties.getGroupDiscountValuePercent()).thenReturn(15);
        when(serviceProperties.getGroupDiscountValueAbsolute()).thenReturn(0);
        var discountRequest = DiscountRequest.builder()
                .quantity(5)
                .price(valueOf(2800))
                .build();

        var response = groupDiscountHandler.calculateDiscount(discountRequest);

        assertThat(response).isNotNull();
        assertThat(response.getType()).isEqualTo(GROUP_BOOKING);
        assertThat(response.getStatus()).isEqualTo(DiscountStatus.ACTIVE);
        assertThat(response.getPriceForOne().compareTo(valueOf(2380))).isEqualTo(0);
        assertThat(response.getTotalCost().compareTo(valueOf(11900))).isEqualTo(0);
        assertThat(response.getQuantity()).isEqualTo(5);
        assertThat(response.getDiscountPercent()).isEqualTo(15);
        assertThat(response.getDiscountAbsolute()).isEqualTo(0);
    }

    @Test
    public void calculate_forOnePlace_shouldReturnNull() {
        when(serviceProperties.isGroupDiscountEnabled()).thenReturn(true);
        when(serviceProperties.getGroupDiscountMinPlaces()).thenReturn(5);
        var discountRequest = DiscountRequest.builder()
                .quantity(1)
                .price(valueOf(2800))
                .build();

        var response = groupDiscountHandler.calculateDiscount(discountRequest);

        assertThat(response).isNull();
    }

    @Test
    public void calculate_withNotEnabledDiscount_shouldReturnNull() {
        when(serviceProperties.isGroupDiscountEnabled()).thenReturn(false);
        var discountRequest = DiscountRequest.builder()
                .quantity(1)
                .price(valueOf(2800))
                .build();

        var response = groupDiscountHandler.calculateDiscount(discountRequest);

        assertThat(response).isNull();
    }


}
