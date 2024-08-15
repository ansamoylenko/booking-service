package com.samoylenko.bookingservice.model.converter;

import com.samoylenko.bookingservice.model.voucher.DiscountType;
import lombok.AllArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class StringToVoucherTypeConverter implements Converter<String, DiscountType> {

    @Override
    public DiscountType convert(String type) {
        return DiscountType.fromDescription(type);
    }
}
