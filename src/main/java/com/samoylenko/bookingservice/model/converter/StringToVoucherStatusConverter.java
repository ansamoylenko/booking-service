package com.samoylenko.bookingservice.model.converter;

import com.samoylenko.bookingservice.model.voucher.VoucherStatus;
import lombok.AllArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class StringToVoucherStatusConverter implements Converter<String, VoucherStatus> {

    @Override
    public VoucherStatus convert(String value) {
        for (VoucherStatus status : VoucherStatus.values()) {
            if (status.message.equals(value)) {
                return status;
            }
        }
        return null;
    }
}
