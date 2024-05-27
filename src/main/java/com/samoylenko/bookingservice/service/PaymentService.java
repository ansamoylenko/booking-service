package com.samoylenko.bookingservice.service;

import com.samoylenko.bookingservice.model.dto.payment.PaymentCreateDto;
import com.samoylenko.bookingservice.model.dto.payment.PaymentDto;
import com.samoylenko.bookingservice.model.dto.request.PaymentRequest;
import com.samoylenko.bookingservice.model.entity.PaymentEntity;
import com.samoylenko.bookingservice.model.exception.PaymentException;
import com.samoylenko.bookingservice.model.exception.PaymentNotFoundException;
import com.samoylenko.bookingservice.model.spec.PaymentSpecification;
import com.samoylenko.bookingservice.model.status.PaymentStatus;
import com.samoylenko.bookingservice.repository.PaymentRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final ModelMapper modelMapper;
    private final Integer timeToPayInMinutes = 15;

    public PaymentDto create(@Valid PaymentCreateDto paymentCreateDto) throws PaymentException {
        var priceForOne = paymentCreateDto.getPriceForOne();
        var totalCost = paymentCreateDto.getAmount() * priceForOne;

        if (paymentCreateDto.getCertificate() != null) {
            totalCost = (paymentCreateDto.getAmount() - 1) * priceForOne;
            priceForOne = totalCost / paymentCreateDto.getAmount();
        } else if (paymentCreateDto.getPromoCode() != null) {
            priceForOne = priceForOne - 300;
            totalCost = paymentCreateDto.getAmount() * priceForOne;
        }
        var link = getInvoiceLink(paymentCreateDto);
        var latestPaymentTime = Instant.now().plus(timeToPayInMinutes, ChronoUnit.MINUTES);

        var paymentEntity = paymentRepository.save(PaymentEntity.builder()
                .status(PaymentStatus.PENDING)
                .orderId(paymentCreateDto.getOrderId())
                .serviceName(paymentCreateDto.getServiceName())
                .priceForOne(paymentCreateDto.getPriceForOne())
                .amount(paymentCreateDto.getAmount())
                .priceForOne(priceForOne)
                .totalCost(totalCost)
                .link(link)
                .latestPaymentTime(latestPaymentTime)
                .build());
        return getPaymentForUser(paymentEntity.getId());
    }

    public PaymentDto getPaymentForUser(@NotBlank String id) {
        var payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
        return modelMapper
                .map(payment, PaymentDto.class)
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
