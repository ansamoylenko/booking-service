package com.samoylenko.bookingservice.service;

import com.samoylenko.bookingservice.model.dto.DefaultClientDtoBuilder;
import com.samoylenko.bookingservice.repository.*;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.TestConstructor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class ClientServiceTest extends BaseServiceTest {
    private final ClientService clientService;
    private final ModelMapper modelMapper;

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


    public ClientServiceTest(ClientService clientService, WalkRepository walkRepository, RouteRepository routeRepository, EmployeeRepository employeeRepository, BookingRepository bookingRepository, ClientRepository clientRepository, PaymentRepository paymentRepository) {
        super(walkRepository, routeRepository, employeeRepository, bookingRepository, clientRepository, paymentRepository);
        this.clientService = clientService;
        this.modelMapper = new ModelMapper();
    }

    @Test
    public void create_shouldClientEntity() {
        var client = DefaultClientDtoBuilder.of().build();

        var entity = clientService.createIfNotExist(client);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isNotNull();
        assertThat(entity.getPhone()).isEqualTo(client.getPhone());
        assertThat(entity.getEmail()).isEqualTo(client.getEmail());
    }

    @Test
    public void create_withIncorrectPhone_shouldThrowValidationException() {
        var client = DefaultClientDtoBuilder.of()
                .withPhone("8888888888").build();

        assertThatThrownBy(() -> clientService.createIfNotExist(client))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Номер телефона должен быть валидным");
    }
}