package com.samoylenko.bookingservice.service;

import com.samoylenko.bookingservice.model.dto.payment.PaymentCreateDto;
import com.samoylenko.bookingservice.model.dto.payment.PaymentDto;
import com.samoylenko.bookingservice.model.dto.request.PaymentRequest;
import com.samoylenko.bookingservice.model.entity.PaymentEntity;
import com.samoylenko.bookingservice.model.exception.PaymentException;
import com.samoylenko.bookingservice.model.exception.PaymentNotFoundException;
import com.samoylenko.bookingservice.model.spec.PaymentSpecification;
import com.samoylenko.bookingservice.model.status.PaymentStatus;
import com.samoylenko.bookingservice.model.status.ValidateResult;
import com.samoylenko.bookingservice.model.voucher.VoucherEntity;
import com.samoylenko.bookingservice.repository.PaymentRepository;
import com.samoylenko.bookingservice.repository.VoucherRepository;
import com.samoylenko.bookingservice.service.handler.VoucherHandler;
import com.samoylenko.bookingservice.service.handler.VoucherHandlerFactory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static java.time.Duration.between;

@Service
@Validated
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final ModelMapper modelMapper;
    private final VoucherRepository voucherRepository;
    private final VoucherHandler voucherHandler;
    private static final Integer timeToPayInMinutes = 15;

    public PaymentService(PaymentRepository paymentRepository, ModelMapper modelMapper, VoucherRepository voucherRepository, VoucherHandlerFactory voucherHandlerFactory) {
        this.paymentRepository = paymentRepository;
        this.modelMapper = modelMapper;
        this.voucherRepository = voucherRepository;
        this.voucherHandler = voucherHandlerFactory.getVoucherHandler();
    }

    public PaymentDto create(@Valid PaymentCreateDto paymentCreateDto) throws PaymentException {
        VoucherEntity voucher = null;
        var price = paymentCreateDto.getPriceForOne();
        var amount = paymentCreateDto.getAmount();
        var cost = price * amount;

        if (paymentCreateDto.getPromoCode() != null) {
            var request = VoucherHandler.AppliementRequest.builder()
                    .quantity(amount)
                    .routeId(paymentCreateDto.getRouteId())
                    .voucherCode(paymentCreateDto.getPromoCode())
                    .price(price)
                    .build();

            var response = voucherHandler.apply(request);
            price = response.getPrice();
            cost = response.getCost();

            if (response.getAppliementResult().getStatus().equals(ValidateResult.Status.VALID)) {
                voucher = voucherRepository.findById(response.getVoucherId()).orElse(null);
            }
        }

        var link = getInvoiceLink(paymentCreateDto); //TODO change request
        var latestPaymentTime = Instant.now().plus(timeToPayInMinutes, ChronoUnit.MINUTES);

        var paymentEntity = paymentRepository.save(PaymentEntity.builder()
                .status(PaymentStatus.PENDING)
                .orderId(paymentCreateDto.getOrderId())
                .serviceName(paymentCreateDto.getServiceName())
                .priceForOne(paymentCreateDto.getPriceForOne())
                .amount(paymentCreateDto.getAmount())
                .priceForOne(price)
                .totalCost(cost)
                .link(link)
                .latestPaymentTime(latestPaymentTime)
                .voucher(voucher)
                .build());
        return getPaymentForUser(paymentEntity.getId());
    }

    public PaymentDto getPaymentForUser(@NotBlank String id) {
        var payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
        var promocode = payment.getVoucher() == null ? null : payment.getVoucher().getId();
        return modelMapper
                .map(payment, PaymentDto.class)
                .withPromocode(promocode)
                .withTimeToPay(getTimeToPay(payment.getLatestPaymentTime()));
    }

    private Duration getTimeToPay(Instant latestPaymentTime) {
        return between(Instant.now(), latestPaymentTime).compareTo(Duration.ofMinutes(0)) > 0 ?
                between(Instant.now(), latestPaymentTime) :
                Duration.ofMinutes(0);
    }

    public PaymentDto getPaymentForAdmin(@NotBlank String id) {
        var payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
        return PaymentDto.builder()
                .id(payment.getId())
                .createdDate(payment.getCreatedDate())
                .lastModifiedDate(payment.getLastModifiedDate())
                .status(payment.getStatus())
                .orderId(payment.getOrderId())
                .serviceName(payment.getServiceName())
                .amount(payment.getAmount())
                .priceForOne(payment.getPriceForOne())
                .totalCost(payment.getTotalCost())
                .timeToPay(getTimeToPay(payment.getLatestPaymentTime()))
                .link(payment.getLink())
                .build();
    }


    public Page<PaymentDto> getPayments(@Valid PaymentRequest request) {
        var spec = PaymentSpecification.withStatus(request.getStatus());
        return paymentRepository
                .findAll(spec, request.getPageRequest())
                .map(element -> modelMapper.map(element, PaymentDto.class)
                        .withTimeToPay(getTimeToPay(element.getLatestPaymentTime())));
    }

    private String getInvoiceLink(PaymentCreateDto paymentCreateDto) {
        return "invoice_link_example";
    }
}
