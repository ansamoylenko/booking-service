package com.samoylenko.bookingservice.service.handler;

import com.samoylenko.bookingservice.service.VoucherService;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class VoucherHandlerFactory {
    private final VoucherHandler voucherHandler;

    public VoucherHandlerFactory(VoucherService voucherService) {
        var certificateHandler = new CertificateHandler(null, voucherService);
        this.voucherHandler = new PromocodHandler(certificateHandler, voucherService);
    }
}
