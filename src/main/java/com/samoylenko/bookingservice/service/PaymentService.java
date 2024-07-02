package com.samoylenko.bookingservice.service;

import com.samoylenko.bookingservice.config.ServiceProperties;
import com.samoylenko.bookingservice.model.dto.payment.InvoiceCreateDto;
import com.samoylenko.bookingservice.model.dto.payment.PaymentCreateDto;
import com.samoylenko.bookingservice.model.dto.payment.PaymentDto;
import com.samoylenko.bookingservice.model.dto.payment.paykeeper.InvoiceDto;
import com.samoylenko.bookingservice.model.dto.request.PaymentRequest;
import com.samoylenko.bookingservice.model.entity.PaymentEntity;
import com.samoylenko.bookingservice.model.exception.EntityCreateException;
import com.samoylenko.bookingservice.model.exception.PaymentException;
import com.samoylenko.bookingservice.model.exception.PaymentNotFoundException;
import com.samoylenko.bookingservice.model.promotion.DiscountDto;
import com.samoylenko.bookingservice.model.promotion.DiscountRequest;
import com.samoylenko.bookingservice.model.spec.PaymentSpecification;
import com.samoylenko.bookingservice.model.status.PaymentStatus;
import com.samoylenko.bookingservice.repository.BookingRepository;
import com.samoylenko.bookingservice.repository.PaymentRepository;
import com.samoylenko.bookingservice.service.discount.DiscountHandler;
import com.samoylenko.bookingservice.service.discount.DiscountManager;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.time.Instant;

import static com.samoylenko.bookingservice.model.exception.EntityType.PAYMENT;
import static com.samoylenko.bookingservice.model.status.PaymentStatus.PAID;
import static com.samoylenko.bookingservice.model.status.PaymentStatus.PENDING;
import static java.math.BigDecimal.ZERO;
import static java.time.Duration.between;
import static java.time.Instant.now;

@Slf4j
@Service
@Validated
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final ModelMapper mapper;
    private final DiscountHandler discountManager;
    private final PayKeeperClient payKeeper;
    private final ServiceProperties properties;

    @PostConstruct
    public void init() {
        mapper.createTypeMap(PaymentEntity.class, PaymentDto.class)
                .addMappings(mapper -> mapper.map(src -> src.getBooking().getId(), PaymentDto::setBookingId));

        mapper.createTypeMap(PaymentEntity.class, InvoiceDto.class)
                .addMappings(mapper -> mapper.map(PaymentEntity::getInvoiceId, InvoiceDto::setInvoiceId))
                .addMappings(mapper -> mapper.map(PaymentEntity::getInvoiceUrl, InvoiceDto::setInvoiceUrl))
                .addMappings(mapper -> mapper.map(PaymentEntity::getLatestPaymentTime, InvoiceDto::setLatestPaymentTime));


        mapper.createTypeMap(PaymentEntity.class, DiscountDto.class)
                .addMappings(mapper -> mapper.map(PaymentEntity::getDiscountType, DiscountDto::setType))
                .addMappings(mapper -> mapper.map(PaymentEntity::getDiscountStatus, DiscountDto::setStatus))
                .addMappings(mapper -> mapper.map(src -> src.getVoucher().getCode(), DiscountDto::setCode))
                .addMappings(mapper -> mapper.map(PaymentEntity::getPriceForOne, DiscountDto::setPriceForOne))
                .addMappings(mapper -> mapper.map(PaymentEntity::getTotalCost, DiscountDto::setTotalCost))
                .addMappings(mapper -> mapper.map(PaymentEntity::getQuantity, DiscountDto::setQuantity))
                .addMappings(mapper -> mapper.map(PaymentEntity::getDiscountPercent, DiscountDto::setDiscountPercent))
                .addMappings(mapper -> mapper.map(PaymentEntity::getDiscountAbsolute, DiscountDto::setDiscountAbsolute));

        mapper.createTypeMap(PaymentCreateDto.class, DiscountRequest.class)
                .addMappings(mapper -> mapper.map(PaymentCreateDto::getVoucher, DiscountRequest::setCode))
                .addMappings(mapper -> mapper.map(PaymentCreateDto::getQuantity, DiscountRequest::setQuantity))
                .addMappings(mapper -> mapper.map(PaymentCreateDto::getRouteId, DiscountRequest::setRouteId))
                .addMappings(mapper -> mapper.map(PaymentCreateDto::getPriceForOne, DiscountRequest::setPrice))
                .addMappings(mapper -> mapper.map(src -> src.getClient().getPhone(), DiscountRequest::setPhone));

    }

    public PaymentService(
            PaymentRepository paymentRepository,
            ModelMapper mapper,
            DiscountManager discountManager,
            PayKeeperClient payKeeper,
            ServiceProperties properties,
            BookingRepository bookingRepository
    ) {
        this.paymentRepository = paymentRepository;
        this.mapper = mapper;
        this.discountManager = discountManager;
        this.payKeeper = payKeeper;
        this.properties = properties;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public PaymentDto createPaymentDocument(@Valid PaymentCreateDto createDto) throws PaymentException {
        try {
            log.info("Attempting to create payment document: {}", createDto);
            var discountRequest = mapper.map(createDto, DiscountRequest.class);
            log.debug("Attempting to apply discount: {}", discountRequest);
            var discount = discountManager.applyDiscount(discountRequest);
            var booking = bookingRepository.getReferenceById(createDto.getBookingId());
            log.debug("Applying discount: {}", discount);
            var paymentBuilder = PaymentEntity.builder()
                    .status(PENDING)
                    .booking(booking)
                    .quantity(createDto.getQuantity())
                    .priceForOne(discount.getPriceForOne())
                    .totalCost(discount.getTotalCost())
                    .discountAbsolute(discount.getDiscountAbsolute())
                    .discountPercent(discount.getDiscountPercent())
                    .discountCode(discount.getCode())
                    .discountType(discount.getType())
                    .discountStatus(discount.getStatus());

            if (discount.getTotalCost().compareTo(ZERO) > 0) {
                var clientName = "%s %s".formatted(createDto.getClient().getLastName(), createDto.getClient().getFirstName());
                var invoiceCreateDto = InvoiceCreateDto.builder()
                        .clientId(clientName)
                        .orderId(createDto.getBookingId())
                        .clientPhone(createDto.getClient().getPhone())
                        .clientEmail(createDto.getClient().getEmail())
                        .quantity(createDto.getQuantity())
                        .price(discount.getPriceForOne())
                        .cost(discount.getTotalCost())
                        .expiry(createDto.getExpiryTime())
                        .build();
                log.debug("Attempting to create invoice: {}", invoiceCreateDto);
                var invoiceResponse = payKeeper.createInvoice(invoiceCreateDto);
                log.debug("Invoice created: {}", invoiceResponse);
                paymentBuilder.latestPaymentTime(createDto.getExpiryTime());
                paymentBuilder.invoiceId(invoiceResponse.id());
                paymentBuilder.invoiceUrl(invoiceResponse.url());
            } else {
                paymentBuilder.status(PAID);
            }
            var payment = paymentRepository.save(paymentBuilder.build());
            var paymentDto = toPaymentDto(payment);
            log.info("Payment document has been created: {}", paymentDto);
            return paymentDto;
        } catch (Exception e) {
            throw new EntityCreateException(PAYMENT, e);
        }
    }

    public PaymentStatus checkPaymentDocument(@NotBlank String paymentId) {
        var payment = getPaymentEntity(paymentId);
        Assert.hasText(payment.getInvoiceId(), "Invoice id has not been found for payment " + paymentId);
        var invoice = payKeeper.getInvoiceInfo(payment.getInvoiceId());

        if (invoice.getStatus().equals("paid")) {
            Assert.isTrue(payment.getBooking().getId().equals(invoice.getOrderId()),
                    "Invoice id has not been matched for invoice: %s, actual: %s"
                            .formatted(payment.getInvoiceId(), invoice.getOrderId()));
            payment.setStatus(PAID);
            paymentRepository.save(payment);
        } else if (payment.getLatestPaymentTime().isBefore(now())) {
            payment.setStatus(PaymentStatus.EXPIRED);
            paymentRepository.save(payment);
        }
        return payment.getStatus();
    }


    private PaymentDto toPaymentDto(PaymentEntity entity) {
        var dto = mapper.map(entity, PaymentDto.class);
        var invoiceDto = mapper.map(entity, InvoiceDto.class);
        var discountDto = mapper.map(entity, DiscountDto.class);
        dto.setInvoice(invoiceDto);
        dto.setDiscount(discountDto);
        return dto;
    }

    public PaymentDto getPaymentById(@NotBlank String id) {
        var payment = getPaymentEntity(id);
        return toPaymentDto(payment);
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

    public Page<PaymentDto> getPayments(@Valid PaymentRequest request) {
        var spec = PaymentSpecification.withStatus(request.getStatus());
        return paymentRepository
                .findAll(spec, request.getPageRequest())
                .map(element -> mapper.map(element, PaymentDto.class));
    }

    public void setStatus(String id, PaymentStatus paymentStatus) {
        var payment = getPaymentEntity(id);
        var oldStatus = payment.getStatus();
        payment.setStatus(paymentStatus);
        paymentRepository.save(payment);
        log.info("Updated status of payment {} from {} to {}", id, oldStatus, paymentStatus);
    }
}

