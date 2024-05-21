package com.samoylenko.bookingservice.repository;

import com.samoylenko.bookingservice.model.entity.DefaultPaymentEntityBuilder;
import com.samoylenko.bookingservice.model.spec.PaymentSpecification;
import com.samoylenko.bookingservice.model.status.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PaymentRepositoryTest extends BaseRepositoryTest {
    @Autowired
    private PaymentRepository paymentRepository;

    @BeforeEach
    public void setUp() {
        paymentRepository.deleteAll();
    }

    @Test
    public void save_shouldSavePayment() {
        var payment = DefaultPaymentEntityBuilder.of().build();

        var savedPayment = paymentRepository.save(payment);

        assertThat(savedPayment).isNotNull();
        assertThat(savedPayment.getId()).isNotNull();
        assertThat(savedPayment.getCreatedDate()).isNotNull();
        assertThat(savedPayment.getLastModifiedDate()).isNotNull();
        assertThat(savedPayment.getVersion()).isNotNull();
        assertThat(payment).isEqualTo(savedPayment);
    }

    @Test
    public void findById_shouldReturnPayment() {
        var payment = DefaultPaymentEntityBuilder.of().build();
        var savedPayment = paymentRepository.save(payment);

        var foundPayment = paymentRepository.findById(savedPayment.getId());

        assertThat(foundPayment).isNotNull();
        assertThat(foundPayment.isPresent()).isTrue();
        assertThat(foundPayment.get()).isEqualTo(savedPayment);
    }

    @Test
    public void findAll_byStatus_shouldReturnFilteredPayment() {
        var payment1 = DefaultPaymentEntityBuilder.of().withStatus(PaymentStatus.PAID).build();
        var payment2 = DefaultPaymentEntityBuilder.of().withStatus(PaymentStatus.PAID).build();
        var payment3 = DefaultPaymentEntityBuilder.of().withStatus(PaymentStatus.CANCELED).build();
        var savedPayment = paymentRepository.saveAll(List.of(payment1, payment2, payment3));
        var spec = PaymentSpecification.withStatus(PaymentStatus.PAID);

        var foundPayments = paymentRepository.findAll(spec);

        assertThat(foundPayments.size()).isEqualTo(2);
        assertThat(foundPayments).contains(payment1, payment2);
    }

    @Test
    void deleteById_shouldDeletePayment() {
        var payment = DefaultPaymentEntityBuilder.of().build();
        var savedPayment = paymentRepository.save(payment);

        var paymentBeforeDelete = paymentRepository.findById(savedPayment.getId());
        paymentRepository.deleteById(savedPayment.getId());
        var paymentAfterDelete = paymentRepository.findById(savedPayment.getId());

        assertThat(paymentBeforeDelete).isNotEmpty();
        assertThat(paymentAfterDelete).isEmpty();
    }
}
