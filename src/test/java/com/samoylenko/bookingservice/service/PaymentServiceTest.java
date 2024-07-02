package com.samoylenko.bookingservice.service;

import com.samoylenko.bookingservice.model.dto.client.ClientDto;
import com.samoylenko.bookingservice.model.dto.payment.InvoiceCreateDto;
import com.samoylenko.bookingservice.model.dto.payment.PaymentCreateDto;
import com.samoylenko.bookingservice.model.dto.payment.paykeeper.InvoiceResponse;
import com.samoylenko.bookingservice.model.entity.DefaultBookingEntityBuilder;
import com.samoylenko.bookingservice.model.entity.DefaultClientEntityBuilder;
import com.samoylenko.bookingservice.model.entity.DefaultPaymentEntityBuilder;
import com.samoylenko.bookingservice.model.promotion.DiscountRequest;
import com.samoylenko.bookingservice.model.promotion.DiscountStatus;
import com.samoylenko.bookingservice.model.promotion.VoucherStatus;
import com.samoylenko.bookingservice.model.status.PaymentStatus;
import com.samoylenko.bookingservice.model.voucher.DiscountType;
import com.samoylenko.bookingservice.model.voucher.VoucherDto;
import com.samoylenko.bookingservice.repository.*;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestConstructor;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static java.math.BigDecimal.valueOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class PaymentServiceTest extends BaseServiceTest {
    private final PaymentService paymentService;
    private final ModelMapper modelMapper;
    @MockBean
    private PromotionService promotionService;

    @MockBean
    private PayKeeperClient payKeeperClient;

    @BeforeEach
    public void setUp() {
        bookingRepository.deleteAll();
        paymentRepository.deleteAll();
        bookingRepository.deleteAll();
        walkRepository.deleteAll();
        routeRepository.deleteAll();
        employeeRepository.deleteAll();
        clientRepository.deleteAll();
    }


    public PaymentServiceTest(PaymentService paymentService, WalkRepository walkRepository, RouteRepository routeRepository, EmployeeRepository employeeRepository, BookingRepository bookingRepository, ClientRepository clientRepository, PaymentRepository paymentRepository) {
        super(walkRepository, routeRepository, employeeRepository, bookingRepository, clientRepository, paymentRepository);
        this.paymentService = paymentService;
        this.modelMapper = new ModelMapper();
    }

    @Test
    public void create_shouldReturnPaymentDto() {
        var client = clientRepository.save(DefaultClientEntityBuilder.of().build());
        var clientDto = modelMapper.map(client, ClientDto.class);
        var booking = bookingRepository.save(DefaultBookingEntityBuilder.of().withClient(client).build());
        var paymentCreateDto = PaymentCreateDto.builder()
                .bookingId(booking.getId())
                .routeId("routeId")
                .serviceName("serviceName")
                .client(clientDto)
                .quantity(2)
                .priceForOne(valueOf(1000))
                .expiryTime(Instant.now().plus(15, ChronoUnit.MINUTES))
                .build();
        when(payKeeperClient.createInvoice(any(InvoiceCreateDto.class)))
                .thenReturn(new InvoiceResponse("invoiceId", "invoiceUrl"));

        var payment = paymentService.createPaymentDocument(paymentCreateDto);

        assertThat(payment).isNotNull();
        assertThat(payment.getId()).isNotNull();
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(payment.getBookingId()).isEqualTo(booking.getId());
        assertThat(payment.getQuantity()).isEqualTo(paymentCreateDto.getQuantity());
        assertThat(payment.getPriceForOne().compareTo(valueOf(1000))).isEqualTo(0);
        assertThat(payment.getTotalCost().compareTo(valueOf(2000))).isEqualTo(0);
        assertThat(payment.getInvoice()).isNotNull();
        assertThat(payment.getInvoice().getInvoiceUrl()).isNotNull();
        assertThat(payment.getInvoice().getInvoiceId()).isNotNull();
        assertThat(payment.getInvoice().getLatestPaymentTime()).isNotNull();
        assertThat(payment.getDiscount()).isNotNull();
        assertThat(payment.getDiscount().getType()).isEqualTo(DiscountType.NONE);
        assertThat(payment.getDiscount().getStatus()).isEqualTo(DiscountStatus.NONE);
        assertThat(payment.getDiscount().getDiscountAbsolute()).isEqualTo(0);
        assertThat(payment.getDiscount().getDiscountPercent()).isEqualTo(0);
        assertThat(payment.getDiscount().getTotalCost().compareTo(valueOf(2000))).isEqualTo(0);
        assertThat(payment.getDiscount().getPriceForOne().compareTo(valueOf(1000))).isEqualTo(0);
    }

    @Test
    public void create_withInvalidData_shouldThrowValidationException() {
        var paymentCreateDto = PaymentCreateDto.builder().build();

        assertThatThrownBy(() -> paymentService.createPaymentDocument(paymentCreateDto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("bookingId: не должно быть пустым")
                .hasMessageContaining("routeId: не должно быть пустым")
                .hasMessageContaining("serviceName: не должно быть пустым")
                .hasMessageContaining("quantity: не должно равняться null")
                .hasMessageContaining("priceForOne: не должно равняться null")
                .hasMessageContaining("client: не должно равняться null")
                .hasMessageContaining("expiryTime: не должно равняться null");
    }

    @Test
    public void create_withCertificate_shouldReturnPaymentDto() {
        var client = clientRepository.save(DefaultClientEntityBuilder.of().build());
        var clientDto = modelMapper.map(client, ClientDto.class);
        var booking = bookingRepository.save(DefaultBookingEntityBuilder.of().withClient(client).build());
        var paymentCreateDto = PaymentCreateDto.builder()
                .bookingId(booking.getId())
                .routeId("routeId")
                .serviceName("serviceName")
                .client(clientDto)
                .quantity(2)
                .priceForOne(valueOf(3000))
                .voucher("certificate")
                .expiryTime(Instant.now().plus(15, ChronoUnit.MINUTES))
                .build();
        var voucherDto = VoucherDto.builder()
                .type(DiscountType.CERTIFICATE)
                .status(VoucherStatus.ACTIVE)
                .discountAbsolute(3200)
                .build();
        when(promotionService.getVoucherByCode(anyString())).thenReturn(voucherDto);
        doNothing().when(promotionService).applyVoucher(any(DiscountRequest.class));
        when(payKeeperClient.createInvoice(any(InvoiceCreateDto.class)))
                .thenReturn(new InvoiceResponse("invoiceId", "invoiceUrl"));

        var payment = paymentService.createPaymentDocument(paymentCreateDto);

        assertThat(payment).isNotNull();
        assertThat(payment.getTotalCost().compareTo(valueOf(2800))).isEqualTo(0);
        assertThat(payment.getPriceForOne().compareTo(valueOf(1400))).isEqualTo(0);
    }

    @Test
    public void create_withCertificateWthBiggerValue_shouldReturnPaymentDto() {
        var client = clientRepository.save(DefaultClientEntityBuilder.of().build());
        var clientDto = modelMapper.map(client, ClientDto.class);
        var booking = bookingRepository.save(DefaultBookingEntityBuilder.of().withClient(client).build());
        var paymentCreateDto = PaymentCreateDto.builder()
                .bookingId(booking.getId())
                .routeId("routeId")
                .serviceName("serviceName")
                .client(clientDto)
                .quantity(2)
                .priceForOne(valueOf(3000))
                .voucher("certificate")
                .expiryTime(Instant.now().plus(15, ChronoUnit.MINUTES))
                .build();
        var voucherDto = VoucherDto.builder()
                .type(DiscountType.CERTIFICATE)
                .status(VoucherStatus.ACTIVE)
                .discountAbsolute(10000)
                .build();
        when(promotionService.getVoucherByCode(anyString())).thenReturn(voucherDto);
        doNothing().when(promotionService).applyVoucher(any(DiscountRequest.class));

        var payment = paymentService.createPaymentDocument(paymentCreateDto);

        assertThat(payment).isNotNull();
        assertThat(payment.getTotalCost().compareTo(valueOf(0))).isEqualTo(0);
        assertThat(payment.getPriceForOne().compareTo(valueOf(0))).isEqualTo(0);
        verify(payKeeperClient, times(0)).createInvoice(any());
    }

    @Test
    public void create_withPromoCode_shouldReturnPaymentDto() {
        var client = clientRepository.save(DefaultClientEntityBuilder.of().build());
        var clientDto = modelMapper.map(client, ClientDto.class);
        var booking = bookingRepository.save(DefaultBookingEntityBuilder.of().withClient(client).build());
        var paymentCreateDto = PaymentCreateDto.builder()
                .bookingId(booking.getId())
                .routeId("routeId")
                .serviceName("serviceName")
                .client(clientDto)
                .quantity(2)
                .priceForOne(valueOf(3000))
                .voucher("promoCode")
                .expiryTime(Instant.now().plus(15, ChronoUnit.MINUTES))
                .build();
        var voucherDto = VoucherDto.builder()
                .type(DiscountType.PROMO_CODE)
                .status(VoucherStatus.ACTIVE)
                .discountPercent(15)
                .build();
        when(promotionService.getVoucherByCode(anyString())).thenReturn(voucherDto);
        doNothing().when(promotionService).applyVoucher(any(DiscountRequest.class));
        when(payKeeperClient.getToken()).thenReturn("token");
        when(payKeeperClient.createInvoice(any(InvoiceCreateDto.class)))
                .thenReturn(new InvoiceResponse("invoiceId", "invoiceUrl"));

        var payment = paymentService.createPaymentDocument(paymentCreateDto);

        assertThat(payment).isNotNull();
        assertThat(payment.getTotalCost().compareTo(valueOf(5100))).isEqualTo(0);
        assertThat(payment.getPriceForOne().compareTo(valueOf(2550))).isEqualTo(0);
        assertThat(payment.getQuantity()).isEqualTo(2);
    }

    @Test
    public void getPaymentById_shouldReturnPaymentDto() {
        var payment = paymentRepository.save(DefaultPaymentEntityBuilder.of()
                .withLatestPaymentTime(Instant.now().plus(15, ChronoUnit.MINUTES)).build());

        var found = paymentService.getPaymentById(payment.getId());

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(payment.getId());
        assertThat(found.getStatus()).isEqualTo(payment.getStatus());
        assertThat(found.getTotalCost().compareTo(payment.getTotalCost())).isEqualTo(0);
        assertThat(found.getInvoice().getInvoiceUrl()).isEqualTo(payment.getInvoiceUrl());
        assertThat(found.getInvoice().getLatestPaymentTime()).isNotNull();
    }
}
