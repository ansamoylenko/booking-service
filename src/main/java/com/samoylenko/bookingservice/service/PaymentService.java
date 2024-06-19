package com.samoylenko.bookingservice.service;

import com.samoylenko.bookingservice.model.dto.payment.InvoiceCreateDto;
import com.samoylenko.bookingservice.model.dto.payment.PaymentCreateDto;
import com.samoylenko.bookingservice.model.dto.payment.PaymentDto;
import com.samoylenko.bookingservice.model.dto.payment.paykeeper.ServiceData;
import com.samoylenko.bookingservice.model.dto.payment.paykeeper.ServiceObject;
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
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.time.Duration.between;
import static java.time.Instant.now;

@Service
@Validated
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final ModelMapper modelMapper;
    private final VoucherRepository voucherRepository;
    private final VoucherHandler voucherHandler;
    private static final Integer timeToPayInMinutes = 15;
    private final PayKeeperClient payKeeper;

    public PaymentService(
            PaymentRepository paymentRepository,
            ModelMapper modelMapper,
            VoucherRepository voucherRepository,
            VoucherHandlerFactory voucherHandlerFactory,
            PayKeeperClient payKeeper
    ) {
        this.paymentRepository = paymentRepository;
        this.modelMapper = modelMapper;
        this.voucherRepository = voucherRepository;
        this.voucherHandler = voucherHandlerFactory.getVoucherHandler();
        this.payKeeper = payKeeper;
    }

    public PaymentDto createInvoice(@Valid PaymentCreateDto createDto) throws PaymentException {
        VoucherEntity voucher = null;
        var price = createDto.getPriceForOne();
        var amount = createDto.getAmount();
        var cost = price * amount;
        if (createDto.getVoucher() != null) {
            var request = VoucherHandler.AppliementRequest.builder()
                    .quantity(amount)
                    .routeId(createDto.getRouteId())
                    .voucherCode(createDto.getVoucher())
                    .price(price)
                    .build();
            var response = voucherHandler.apply(request);
            price = response.getPrice();
            cost = response.getCost();
            if (response.getAppliementResult().getStatus().equals(ValidateResult.Status.VALID)) {
                voucher = voucherRepository.findById(response.getVoucherId()).orElse(null);
            }
        }
        var serviceObject = ServiceObject.builder()
                .name("Прогулка")
                .price(price)
                .quantity(amount)
                .sum(cost)
                .tax("vat0")
                .itemType("service")
                .paymentType("prepay")
                .build();
        var serviceData = new ServiceData(List.of(serviceObject), "ru");
        var clientName = "%s %s".formatted(createDto.getClient().getLastName(), createDto.getClient().getFirstName());
        var paymentData = InvoiceCreateDto.builder()
                .clientId(clientName)
                .orderId(createDto.getOrderId())
                .clientPhone(createDto.getClient().getPhone())
                .clientEmail(createDto.getClient().getEmail())
                .serviceData(serviceData)
                .payAmount(BigDecimal.valueOf(cost))
                .expiry(createDto.getExpiryTime())
                .build();
        var paymentEntity = PaymentEntity.builder()
                .orderId(createDto.getOrderId())
                .serviceName(createDto.getServiceName())
                .priceForOne(createDto.getPriceForOne())
                .amount(createDto.getAmount())
                .priceForOne(price)
                .totalCost(cost)
                .voucher(voucher)
                .build();
        if (cost > 0) {
            var invoiceResponse = payKeeper.createInvoice(paymentData);
            var latestPaymentTime = now().plus(timeToPayInMinutes, ChronoUnit.MINUTES);
            paymentEntity.setLatestPaymentTime(latestPaymentTime);
            paymentEntity.setInvoiceId(invoiceResponse.id());
            paymentEntity.setInvoiceUrl(invoiceResponse.url());
            paymentEntity.setStatus(PaymentStatus.PENDING);
        } else {
            paymentEntity.setStatus(PaymentStatus.PAID);
            paymentEntity.setLatestPaymentTime(now());
        }
        paymentEntity = paymentRepository.save(paymentEntity);
        return getPaymentForUser(paymentEntity.getId());
    }

    public PaymentStatus checkInvoice(@NotBlank String paymentId) {
        var payment = getPaymentEntity(paymentId);
        Assert.hasText(payment.getInvoiceId(), "Invoice id has not been found for payment " + paymentId);
        var invoice = payKeeper.getInvoiceInfo(payment.getInvoiceId());

        if (invoice.getStatus().equals("paid")) {
            payment.setStatus(PaymentStatus.PAID);
            paymentRepository.save(payment);
        }
        return payment.getStatus();
    }

    public PaymentDto getPaymentForUser(@NotBlank String id) {
        var payment = getPaymentEntity(id);
        var promocode = payment.getVoucher() == null ? null : payment.getVoucher().getId();
        return modelMapper
                .map(payment, PaymentDto.class)
                .withPromocode(promocode)
                .withTimeToPay(getTimeToPay(payment.getLatestPaymentTime()));
    }

    private PaymentEntity getPaymentEntity(String id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
    }

    private Duration getTimeToPay(Instant latestPaymentTime) {
        return between(now(), latestPaymentTime).compareTo(Duration.ofMinutes(0)) > 0 ?
                between(now(), latestPaymentTime) :
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
                .invoiceUrl(payment.getInvoiceUrl())
                .build();
    }


    public Page<PaymentDto> getPayments(@Valid PaymentRequest request) {
        var spec = PaymentSpecification.withStatus(request.getStatus());
        return paymentRepository
                .findAll(spec, request.getPageRequest())
                .map(element -> modelMapper.map(element, PaymentDto.class)
                        .withTimeToPay(getTimeToPay(element.getLatestPaymentTime())));
    }

    public void setStatus(String id, PaymentStatus paymentStatus) {
        var payment = getPaymentEntity(id);
        payment.setStatus(paymentStatus);
        paymentRepository.save(payment);
    }
}

