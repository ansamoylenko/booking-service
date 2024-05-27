package com.samoylenko.bookingservice.service;

import com.samoylenko.bookingservice.model.dto.client.ClientDto;
import com.samoylenko.bookingservice.model.dto.payment.PaymentCreateDto;
import com.samoylenko.bookingservice.model.entity.*;
import com.samoylenko.bookingservice.model.status.PaymentStatus;
import com.samoylenko.bookingservice.repository.*;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.TestConstructor;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class PaymentServiceTest extends BaseServiceTest {
    private final PaymentService paymentService;
    private final ModelMapper modelMapper;

    public PaymentServiceTest(PaymentService paymentService, WalkRepository walkRepository, RouteRepository routeRepository, EmployeeRepository employeeRepository, BookingRepository bookingRepository, ClientRepository clientRepository, PaymentRepository paymentRepository) {
        super(walkRepository, routeRepository, employeeRepository, bookingRepository, clientRepository, paymentRepository);
        this.paymentService = paymentService;
        this.modelMapper = new ModelMapper();
    }

    @Test
    public void create_shouldReturnPaymentDto() {
        var client = modelMapper.map(DefaultClientEntityBuilder.of().build(), ClientDto.class);
        var paymentCreateDto = PaymentCreateDto.builder()
                .orderId("orderId")
                .serviceName("serviceName")
                .client(client)
                .amount(1)
                .priceForOne(1000)
                .build();

        var payment = paymentService.create(paymentCreateDto);

        assertThat(payment).isNotNull();
        assertThat(payment.getId()).isNotNull();
        assertThat(payment.getOrderId()).isEqualTo(paymentCreateDto.getOrderId());
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(payment.getLink()).isNotNull();
        assertThat(payment.getTimeToPay()).isNotNull();
        assertThat(payment.getServiceName()).isEqualTo(paymentCreateDto.getServiceName());
        assertThat(payment.getPriceForOne()).isEqualTo(paymentCreateDto.getPriceForOne());
        assertThat(payment.getAmount()).isEqualTo(paymentCreateDto.getAmount());
        assertThat(payment.getTotalCost()).isEqualTo(paymentCreateDto.getPriceForOne() * paymentCreateDto.getAmount());
        assertThat(payment.getServiceName()).isNotNull();
    }

    @Test
    public void create_withInvalidData_shouldThrowValidationException() {
        var paymentCreateDto = PaymentCreateDto.builder().build();

        assertThatThrownBy(() -> paymentService.create(paymentCreateDto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("priceForOne: не должно равняться null")
                .hasMessageContaining("orderId: не должно быть пустым")
                .hasMessageContaining("serviceName: не должно быть пустым")
                .hasMessageContaining("client: не должно равняться null")
                .hasMessageContaining("amount: не должно равняться null");
    }

    @Test
    public void create_withCertificate_shouldReturnPaymentDto() {
        var client = modelMapper.map(DefaultClientEntityBuilder.of().build(), ClientDto.class);
        var paymentCreateDto = PaymentCreateDto.builder()
                .orderId("orderId")
                .serviceName("serviceName")
                .client(client)
                .amount(2)
                .priceForOne(3000)
                .certificate("certificate")
                .build();

        var payment = paymentService.create(paymentCreateDto);

        assertThat(payment).isNotNull();
        assertThat(payment.getTotalCost()).isEqualTo(3000);
    }

    @Test
    public void create_withPromoCode_shouldReturnPaymentDto() {
        var client = modelMapper.map(DefaultClientEntityBuilder.of().build(), ClientDto.class);
        var paymentCreateDto = PaymentCreateDto.builder()
                .orderId("orderId")
                .serviceName("serviceName")
                .client(client)
                .amount(2)
                .priceForOne(1000)
                .promoCode("promoCode")
                .build();

        var payment = paymentService.create(paymentCreateDto);

        assertThat(payment).isNotNull();
        assertThat(payment.getServiceName()).isEqualTo(paymentCreateDto.getServiceName());
        assertThat(payment.getTotalCost()).isEqualTo(1400);
        assertThat(payment.getAmount()).isEqualTo(2);
        assertThat(payment.getPriceForOne()).isEqualTo(700);
    }

    @Test
    public void getPaymentById_shouldReturnPaymentDto() {
        var payment = paymentRepository.save(DefaultPaymentEntityBuilder.of()
                .withLatestPaymentTime(Instant.now().plus(15, ChronoUnit.MINUTES)).build());

        var found = paymentService.getPaymentForUser(payment.getId());

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(payment.getId());
        assertThat(found.getOrderId()).isEqualTo(payment.getOrderId());
        assertThat(found.getStatus()).isEqualTo(payment.getStatus());
        assertThat(found.getTotalCost()).isEqualTo(payment.getTotalCost());
        assertThat(found.getLink()).isEqualTo(payment.getLink());
        assertThat(found.getTimeToPay()).isNotNull();
        assertThat(found.getServiceName()).isEqualTo(payment.getServiceName());
    }

    @Test
    public void getPaymentForUser_withExpiredTimeToPay_shouldReturnPaymentDto() {
        var payment = paymentRepository.save(DefaultPaymentEntityBuilder.of()
                .withLatestPaymentTime(Instant.now().minus(15, ChronoUnit.MINUTES)).build());

        var found = paymentService.getPaymentForUser(payment.getId());

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(payment.getId());
        assertThat(found.getOrderId()).isEqualTo(payment.getOrderId());
        assertThat(found.getStatus()).isEqualTo(payment.getStatus());
        assertThat(found.getTotalCost()).isEqualTo(payment.getTotalCost());
        assertThat(found.getLink()).isEqualTo(payment.getLink());
        assertThat(found.getTimeToPay()).isEqualTo(Duration.ofMinutes(0));
        assertThat(found.getServiceName()).isEqualTo(payment.getServiceName());
    }

    @Test
    public void getPaymentForAdmin_shouldReturnPaymentDto() {
        var route = routeRepository.save(DefaultRouteEntityBuilder.of().build());
        var walk = walkRepository.save(DefaultWalkEntityBuilder.of().withRoute(route).build());
        var payment = paymentRepository.save(DefaultPaymentEntityBuilder.of()
                .withLatestPaymentTime(Instant.now().plus(15, ChronoUnit.MINUTES))
                .build());
        var booking = bookingRepository.save(DefaultBookingEntityBuilder.of()
                .withPayment(payment)
                .withWalk(walk)
                .build());

        var found = paymentService.getPaymentForAdmin(payment.getId());

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(payment.getId());
        assertThat(found.getOrderId()).isEqualTo(payment.getOrderId());
        assertThat(found.getStatus()).isEqualTo(payment.getStatus());
        assertThat(found.getTotalCost()).isEqualTo(payment.getTotalCost());
        assertThat(found.getLink()).isEqualTo(payment.getLink());
        assertThat(found.getTimeToPay()).isNotNull();
        assertThat(found.getServiceName()).isEqualTo(payment.getServiceName());
        assertThat(found.getCreatedDate()).isNotNull();
        assertThat(found.getLastModifiedDate()).isNotNull();
    }
}
